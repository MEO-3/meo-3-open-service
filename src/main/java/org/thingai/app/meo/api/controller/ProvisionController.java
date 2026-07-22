package org.thingai.app.meo.api.controller;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.sse.SseClient;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import com.google.gson.JsonObject;
import org.thingai.app.meo.api.dto.MeoDeviceResponse;
import org.thingai.app.meo.api.dto.MeoErrorResponse;
import org.thingai.app.meo.api.dto.MeoProvisionRequest;
import org.thingai.app.meo.callback.ProvisionEventListener;
import org.thingai.app.meo.callback.RequestCallback;
import org.thingai.app.meo.define.ErrorCode;
import org.thingai.app.meo.entity.MeoDeviceProvision;
import org.thingai.app.meo.handler.MeoProvisionHandler;
import org.thingai.app.meo.util.JsonUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// Stepped provisioning endpoints (scan -> connect -> setup -> persist) plus an
// SSE stream mirroring the handler's progress events, so a UI can watch live
// status while the blocking step calls run. The step endpoints' request/response
// contracts are unchanged; the stream is additive visibility.
public class ProvisionController implements ProvisionEventListener {
    private static final int DEFAULT_SCAN_TIMEOUT_MS = 8000;

    private final MeoProvisionHandler provisionHandler;

    // Events arrive from request threads and the MQTT callback thread;
    // copy-on-write iteration keeps the fan-out lock-free.
    private final List<SseClient> sseClients = new CopyOnWriteArrayList<>();

    public ProvisionController(MeoProvisionHandler provisionHandler) {
        this.provisionHandler = provisionHandler;
        provisionHandler.setEventListener(this);
    }

    public void addRoutes(JavalinConfig config) {
        config.routes.get("/api/v1/provision/scan", this::scan);
        config.routes.post("/api/v1/provision/connect", this::connect);
        config.routes.post("/api/v1/provision/setup", this::setup);
        config.routes.post("/api/v1/provision/persist", this::persist);
        config.routes.sse("/api/v1/provision/events", this::events);
    }

    @OpenApi(
            path = "/api/v1/provision/events",
            methods = HttpMethod.GET,
            summary = "Stream provisioning progress (SSE)",
            description = "Server-sent events mirroring provisioning progress. Open this stream, "
                    + "then drive the scan/connect/setup/persist steps and watch events: "
                    + "scan.started, scan.device_found, scan.completed, provision.status, "
                    + "device.persisted. On connect the current session state (if any) is sent "
                    + "as a provision.status event. Event data is JSON.",
            tags = {"provision"},
            responses = {
                    @OpenApiResponse(status = "200", description = "text/event-stream of provisioning events")
            }
    )
    private void events(SseClient client) {
        client.keepAlive();
        client.onClose(() -> sseClients.remove(client));
        sseClients.add(client);

        // Late/reconnecting clients get the in-flight session state up front.
        MeoDeviceProvision session = provisionHandler.currentSession();
        if (session != null) {
            send(client, MeoProvisionHandler.EVENT_PROVISION_STATUS, JsonUtil.toJson(session));
        }
    }

    @Override
    public void onEvent(String event, Object payload) {
        if (sseClients.isEmpty()) {
            return;
        }
        String data = JsonUtil.toJson(payload);
        for (SseClient client : sseClients) {
            send(client, event, data);
        }
    }

    private void send(SseClient client, String event, String data) {
        if (client.terminated()) {
            sseClients.remove(client);
            return;
        }
        try {
            client.sendEvent(event, data);
        } catch (RuntimeException e) {
            sseClients.remove(client);
        }
    }

    @OpenApi(
            path = "/api/v1/provision/scan",
            methods = HttpMethod.GET,
            summary = "Scan for provisionable BLE devices",
            description = "Step 1 of provisioning. Scans for nearby MEO devices advertising the "
                    + "provisioning BLE service. One in-flight session is shared across "
                    + "scan/connect/setup/persist (BLE is single-device).",
            tags = {"provision"},
            queryParams = {
                    @OpenApiParam(name = "timeoutMs", type = Integer.class,
                            description = "Scan duration in milliseconds (default 8000)"),
                    @OpenApiParam(name = "namePrefix",
                            description = "Only return devices whose name starts with this prefix")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = Object[].class),
                            description = "Discovered devices as reported by the BLE service"),
                    @OpenApiResponse(status = "500", content = @OpenApiContent(from = MeoErrorResponse.class))
            }
    )
    private void scan(Context ctx) {
        int timeoutMs = parseTimeout(ctx.queryParam("timeoutMs"));
        provisionHandler.scan(timeoutMs, ctx.queryParam("namePrefix"), new RequestCallback<JsonObject[]>() {
            @Override
            public void onResult(JsonObject[] devices, String message) {
                // Scan results are Gson JsonObjects, which Javalin's Jackson
                // mapper cannot serialize — render them with Gson instead.
                ctx.contentType("application/json").result(JsonUtil.toJson(devices));
            }

            @Override
            public void onFailure(int errorCode, String message) {
                fail(ctx, errorCode, message, 500);
            }
        });
    }

    @OpenApi(
            path = "/api/v1/provision/connect",
            methods = HttpMethod.POST,
            summary = "Connect to a scanned device",
            description = "Step 2 of provisioning. Connects to the device over BLE and reads its "
                    + "identity (MAC address, model, firmware version, capabilities).",
            tags = {"provision"},
            requestBody = @OpenApiRequestBody(required = true,
                    content = @OpenApiContent(from = MeoProvisionRequest.class),
                    description = "Requires bleAddress from a scan result"),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = MeoDeviceProvision.class)),
                    @OpenApiResponse(status = "400", content = @OpenApiContent(from = MeoErrorResponse.class),
                            description = "bleAddress is missing"),
                    @OpenApiResponse(status = "500", content = @OpenApiContent(from = MeoErrorResponse.class))
            }
    )
    private void connect(Context ctx) {
        MeoProvisionRequest request = ctx.bodyAsClass(MeoProvisionRequest.class);
        if (request == null || isBlank(request.getBleAddress())) {
            ctx.status(400).json(error(ErrorCode.PROV_CONNECT_FAILED, "bleAddress is required"));
            return;
        }
        provisionHandler.connect(request.getBleAddress(), new RequestCallback<MeoDeviceProvision>() {
            @Override
            public void onResult(MeoDeviceProvision provision, String message) {
                ctx.json(provision);
            }

            @Override
            public void onFailure(int errorCode, String message) {
                fail(ctx, errorCode, message, 500);
            }
        });
    }

    @OpenApi(
            path = "/api/v1/provision/setup",
            methods = HttpMethod.POST,
            summary = "Send Wi-Fi credentials to the connected device",
            description = "Step 3 of provisioning. Writes the Wi-Fi configuration to the device "
                    + "connected in the previous step and waits for its provision status.",
            tags = {"provision"},
            requestBody = @OpenApiRequestBody(required = true,
                    content = @OpenApiContent(from = MeoProvisionRequest.class),
                    description = "Requires ssid; password is optional for open networks"),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = MeoDeviceProvision.class)),
                    @OpenApiResponse(status = "400", content = @OpenApiContent(from = MeoErrorResponse.class),
                            description = "ssid is missing"),
                    @OpenApiResponse(status = "409", content = @OpenApiContent(from = MeoErrorResponse.class),
                            description = "Device rejected the configuration or no session in flight")
            }
    )
    private void setup(Context ctx) {
        MeoProvisionRequest request = ctx.bodyAsClass(MeoProvisionRequest.class);
        if (request == null || isBlank(request.getSsid())) {
            ctx.status(400).json(error(ErrorCode.PROV_SETUP_FAILED, "ssid is required"));
            return;
        }
        provisionHandler.setupDevice(request.getSsid(), request.getPassword(), new RequestCallback<MeoDeviceProvision>() {
            @Override
            public void onResult(MeoDeviceProvision provision, String message) {
                ctx.json(provision);
            }

            @Override
            public void onFailure(int errorCode, String message) {
                fail(ctx, errorCode, message, 409);
            }
        });
    }

    @OpenApi(
            path = "/api/v1/provision/persist",
            methods = HttpMethod.POST,
            summary = "Persist the provisioned device",
            description = "Step 4 of provisioning. Saves the successfully provisioned device to the "
                    + "gateway database and closes the in-flight session.",
            tags = {"provision"},
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = MeoDeviceResponse.class)),
                    @OpenApiResponse(status = "409", content = @OpenApiContent(from = MeoErrorResponse.class),
                            description = "No provisioned device in flight to persist")
            }
    )
    private void persist(Context ctx) {
        provisionHandler.persistDevice(new RequestCallback<MeoDeviceResponse>() {
            @Override
            public void onResult(MeoDeviceResponse device, String message) {
                ctx.json(device);
            }

            @Override
            public void onFailure(int errorCode, String message) {
                fail(ctx, errorCode, message, 409);
            }
        });
    }

    private void fail(Context ctx, int errorCode, String message, int status) {
        ctx.status(status).json(error(errorCode, message));
    }

    private MeoErrorResponse error(int errorCode, String message) {
        return new MeoErrorResponse(errorCode, message);
    }

    private int parseTimeout(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return DEFAULT_SCAN_TIMEOUT_MS;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
