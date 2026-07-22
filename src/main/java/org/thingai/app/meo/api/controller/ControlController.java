package org.thingai.app.meo.api.controller;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import org.thingai.app.meo.api.dto.MeoCommandRequest;
import org.thingai.app.meo.api.dto.MeoCommandResponse;
import org.thingai.app.meo.api.dto.MeoErrorResponse;
import org.thingai.app.meo.callback.RequestCallback;
import org.thingai.app.meo.define.ErrorCode;
import org.thingai.app.meo.handler.MeoControlHandler;

// Device control endpoint. One route: send a capability command and get the
// device's value back.
public class ControlController {
    // Null when the device MQTT connection failed at startup; the service still
    // serves the rest of the API, so answer 503 instead of throwing.
    private final MeoControlHandler controlHandler;

    public ControlController(MeoControlHandler controlHandler) {
        this.controlHandler = controlHandler;
    }

    public void addRoutes(JavalinConfig config) {
        config.routes.post("/api/v1/devices/{deviceId}/command", this::command);
    }

    @OpenApi(
            path = "/api/v1/devices/{deviceId}/command",
            methods = HttpMethod.POST,
            summary = "Send a command to a device",
            description = "Publishes a command to the device and waits for its reply. There is no "
                    + "verb: the capability id decides whether this reads a sensor (MEO_READ_*), "
                    + "writes an actuator (MEO_WRITE_*), or runs a generic command (MEO_CMD_*). "
                    + "value is only used by writes.",
            tags = {"control"},
            pathParams = {
                    @OpenApiParam(name = "deviceId", required = true)
            },
            requestBody = @OpenApiRequestBody(required = true,
                    content = @OpenApiContent(from = MeoCommandRequest.class)),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = MeoCommandResponse.class)),
                    @OpenApiResponse(status = "400", content = @OpenApiContent(from = MeoErrorResponse.class),
                            description = "Malformed request, or the device does not implement the capability"),
                    @OpenApiResponse(status = "404", content = @OpenApiContent(from = MeoErrorResponse.class),
                            description = "Unknown device"),
                    @OpenApiResponse(status = "502", content = @OpenApiContent(from = MeoErrorResponse.class),
                            description = "Device rejected the command or failed to execute it"),
                    @OpenApiResponse(status = "503", content = @OpenApiContent(from = MeoErrorResponse.class),
                            description = "Device messaging is not connected"),
                    @OpenApiResponse(status = "504", content = @OpenApiContent(from = MeoErrorResponse.class),
                            description = "Device did not reply in time")
            }
    )
    private void command(Context ctx) {
        if (controlHandler == null) {
            fail(ctx, ErrorCode.CONTROL_FAILED, "device messaging is not connected", 503);
            return;
        }
        MeoCommandRequest request = ctx.bodyAsClass(MeoCommandRequest.class);
        if (request == null) {
            fail(ctx, ErrorCode.CONTROL_FAILED, "request body is required", 400);
            return;
        }

        String deviceId = ctx.pathParam("deviceId");
        int cap = request.getCap();
        // The handler blocks and invokes the callback on this request thread.
        controlHandler.sendCommand(deviceId, cap, request.getValue(), new RequestCallback<Double>() {
            @Override
            public void onResult(Double value, String message) {
                ctx.json(MeoCommandResponse.of(deviceId, cap, value));
            }

            @Override
            public void onFailure(int errorCode, String message) {
                fail(ctx, errorCode, message, statusFor(errorCode));
            }
        });
    }

    private int statusFor(int errorCode) {
        if (errorCode == ErrorCode.DEVICE_NOT_FOUND) {
            return 404;
        }
        if (errorCode == ErrorCode.CONTROL_TIMEOUT) {
            return 504;
        }
        if (errorCode == ErrorCode.CONTROL_DEVICE_ERROR) {
            return 502;
        }
        return 400;
    }

    private void fail(Context ctx, int errorCode, String message, int status) {
        ctx.status(status).json(new MeoErrorResponse(errorCode, message));
    }
}
