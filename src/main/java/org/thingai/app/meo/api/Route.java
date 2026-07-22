package org.thingai.app.meo.api;

import io.javalin.config.JavalinConfig;
import org.thingai.app.meo.api.controller.ControlController;
import org.thingai.app.meo.api.controller.DeviceController;
import org.thingai.app.meo.api.controller.ProvisionController;
import org.thingai.app.meo.handler.MeoControlHandler;
import org.thingai.app.meo.handler.MeoDeviceHandler;
import org.thingai.app.meo.handler.MeoProvisionHandler;

// Registers all HTTP routes. Endpoint logic lives in api/controller classes;
// this class only wires handlers to controllers.
public class Route {
    private final JavalinConfig config;
    private final MeoDeviceHandler deviceHandler;
    private final MeoProvisionHandler provisionHandler;
    private final MeoControlHandler controlHandler;

    public Route(JavalinConfig config, MeoDeviceHandler deviceHandler,
                 MeoProvisionHandler provisionHandler, MeoControlHandler controlHandler) {
        this.config = config;
        this.deviceHandler = deviceHandler;
        this.provisionHandler = provisionHandler;
        this.controlHandler = controlHandler;
    }

    public void addRoutes() {
        config.routes.get("/", ctx -> ctx.json("meow"));

        new DeviceController(deviceHandler).addRoutes(config);
        new ProvisionController(provisionHandler).addRoutes(config);
        new ControlController(controlHandler).addRoutes(config);
    }
}
