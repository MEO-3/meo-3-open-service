package org.thingai.app.meo.api;

import io.javalin.config.JavalinConfig;
import org.thingai.app.meo.api.controller.ProvisionController;
import org.thingai.app.meo.handler.MeoDeviceHandler;
import org.thingai.app.meo.handler.MeoProvisionHandler;

// Registers all HTTP routes. Endpoint logic lives in api/controller classes;
// this class only wires handlers to controllers.
public class Route {
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

        new ProvisionController(provisionHandler).addRoutes(config);
    }
}
