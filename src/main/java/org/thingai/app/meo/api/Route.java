package org.thingai.app.meo.api;

import com.google.gson.JsonObject;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import org.thingai.app.meo.api.dto.MeoProvisionRequest;
import org.thingai.app.meo.entity.MeoDevice;
import org.thingai.app.meo.entity.MeoDeviceProvision;
import org.thingai.app.meo.handler.MeoDeviceHandler;
import org.thingai.app.meo.handler.MeoProvisionHandler;
import org.thingai.app.meo.handler.callback.RequestCallback;

import java.util.List;
import java.util.Map;

public class Route {
    private static final int DEFAULT_SCAN_TIMEOUT_MS = 8000;

    private final JavalinConfig config;
    private final MeoDeviceHandler deviceHandler;
    private final MeoProvisionHandler provisionHandler;

    public Route(JavalinConfig config, MeoDeviceHandler deviceHandler, MeoProvisionHandler provisionHandler) {
        this.config = config;
        this.deviceHandler = deviceHandler;
        this.provisionHandler = provisionHandler;
    }

    public void addRoutes() {
        config.routes.get("/", ctx -> ctx.json("meow"));

        config.routes.get("/api/v1/provision/scan", this::scan);
        config.routes.post("/api/v1/provision", this::provision);
    }

    private void scan(Context ctx) {
        int timeoutMs = parseTimeout(ctx.queryParam("timeoutMs"));
        // The handler blocks and invokes the callback on this request thread, so
        // writing the response from the callback is safe with synchronous Javalin.
        provisionHandler.scan(timeoutMs, ctx.queryParam("namePrefix"), new RequestCallback<List<JsonObject>>() {
            @Override
            public void onResult(List<JsonObject> devices, String message) {
                ctx.json(devices);
            }

            @Override
            public void onFailure(Throwable t, String message) {
                ctx.status(500).json(Map.of("error", t.getMessage() != null ? t.getMessage() : "scan failed"));
            }
        });
    }

    private void provision(Context ctx) {
        MeoProvisionRequest request = ctx.bodyAsClass(MeoProvisionRequest.class);
        if (request == null || isBlank(request.getBleAddress())) {
            ctx.status(400).json(Map.of("error", "bleAddress is required"));
            return;
        }
        if (isBlank(request.getSsid())) {
            ctx.status(400).json(Map.of("error", "ssid is required"));
            return;
        }

        MeoDeviceProvision provision = new MeoDeviceProvision();
        provision.setBleAddress(request.getBleAddress());
        provisionHandler.provision(provision, request.getSsid(), request.getPassword(), new RequestCallback<MeoDevice>() {
            @Override
            public void onResult(MeoDevice device, String message) {
                ctx.json(device);
            }

            @Override
            public void onFailure(Throwable t, String message) {
                int status = t instanceof IllegalArgumentException ? 400 : 500;
                ctx.status(status).json(Map.of("error", t.getMessage() != null ? t.getMessage() : "provision failed"));
            }
        });
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
