package org.thingai.app.meo.api.controller;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import org.thingai.app.meo.api.dto.MeoDeviceResponse;
import org.thingai.app.meo.api.dto.MeoErrorResponse;
import org.thingai.app.meo.define.ErrorCode;
import org.thingai.app.meo.entity.MeoDevice;
import org.thingai.app.meo.handler.MeoDeviceHandler;

// CRUD over provisioned devices. Devices are created by the provisioning flow
// (see ProvisionController); this controller only lists, edits user metadata,
// and removes them.
public class DeviceController {
    private final MeoDeviceHandler deviceHandler;

    public DeviceController(MeoDeviceHandler deviceHandler) {
        this.deviceHandler = deviceHandler;
    }

    public void addRoutes(JavalinConfig config) {
        config.routes.get("/api/v1/devices", this::list);
        config.routes.get("/api/v1/devices/{deviceId}", this::get);
        config.routes.put("/api/v1/devices/{deviceId}", this::update);
        config.routes.delete("/api/v1/devices/{deviceId}", this::delete);
    }

    @OpenApi(
            path = "/api/v1/devices",
            methods = HttpMethod.GET,
            summary = "List provisioned devices",
            tags = {"device"},
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = MeoDeviceResponse[].class))
            }
    )
    private void list(Context ctx) {
        MeoDevice[] devices = deviceHandler.getDevices();
        MeoDeviceResponse[] response = new MeoDeviceResponse[devices.length];
        for (int i = 0; i < devices.length; i++) {
            response[i] = toResponse(devices[i]);
        }
        ctx.json(response);
    }

    @OpenApi(
            path = "/api/v1/devices/{deviceId}",
            methods = HttpMethod.GET,
            summary = "Get a device by id",
            tags = {"device"},
            pathParams = {
                    @OpenApiParam(name = "deviceId", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = MeoDeviceResponse.class)),
                    @OpenApiResponse(status = "404", content = @OpenApiContent(from = MeoErrorResponse.class))
            }
    )
    private void get(Context ctx) {
        MeoDevice device = deviceHandler.getDevice(ctx.pathParam("deviceId"));
        if (device == null) {
            notFound(ctx);
            return;
        }
        ctx.json(toResponse(device));
    }

    @OpenApi(
            path = "/api/v1/devices/{deviceId}",
            methods = HttpMethod.PUT,
            summary = "Update a device's user metadata",
            description = "Only name, description and deviceType are updatable. Identity "
                    + "(deviceId, macAddress) and firmware-reported fields (model, fwVersion, "
                    + "transportType) are owned by the provisioning flow and ignored here.",
            tags = {"device"},
            pathParams = {
                    @OpenApiParam(name = "deviceId", required = true)
            },
            requestBody = @OpenApiRequestBody(required = true,
                    content = @OpenApiContent(from = MeoDevice.class)),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = MeoDeviceResponse.class)),
                    @OpenApiResponse(status = "400", content = @OpenApiContent(from = MeoErrorResponse.class)),
                    @OpenApiResponse(status = "404", content = @OpenApiContent(from = MeoErrorResponse.class))
            }
    )
    private void update(Context ctx) {
        MeoDevice update = ctx.bodyAsClass(MeoDevice.class);
        if (update == null) {
            ctx.status(400).json(new MeoErrorResponse(ErrorCode.DEVICE_UPDATE_FAILED, "request body is required"));
            return;
        }
        MeoDevice device = deviceHandler.updateDevice(ctx.pathParam("deviceId"), update);
        if (device == null) {
            notFound(ctx);
            return;
        }
        ctx.json(toResponse(device));
    }

    @OpenApi(
            path = "/api/v1/devices/{deviceId}",
            methods = HttpMethod.DELETE,
            summary = "Delete a device",
            description = "Removes the device and its capability rows.",
            tags = {"device"},
            pathParams = {
                    @OpenApiParam(name = "deviceId", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = MeoDeviceResponse.class),
                            description = "The deleted device"),
                    @OpenApiResponse(status = "404", content = @OpenApiContent(from = MeoErrorResponse.class))
            }
    )
    private void delete(Context ctx) {
        // Capture capabilities before the handler removes their rows.
        String deviceId = ctx.pathParam("deviceId");
        int[] capabilities = deviceHandler.getCapabilities(deviceId);
        MeoDevice device = deviceHandler.deleteDevice(deviceId);
        if (device == null) {
            notFound(ctx);
            return;
        }
        ctx.json(MeoDeviceResponse.of(device, capabilities));
    }

    private MeoDeviceResponse toResponse(MeoDevice device) {
        return MeoDeviceResponse.of(device, deviceHandler.getCapabilities(device.getDeviceId()));
    }

    private void notFound(Context ctx) {
        ctx.status(404).json(new MeoErrorResponse(ErrorCode.DEVICE_NOT_FOUND, "device not found"));
    }
}
