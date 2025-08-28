package org.thingai.meo.controller;

import io.javalin.Javalin;

public class Route {
    private static Javalin app;

    public Route(Javalin appInstance) {
        app = appInstance;
    }

    public void addRoutes() {
        // Root routes
        app.get("/", ctx -> ctx.result("Welcome to Meo Service!"));

        // MeoServiceController routes
        app.get("/service/status", ControllerMeoService::getServiceStatus);

        // MeoDeviceController routes
        app.get("/devices", ControllerMeoDevice::getAllDevices);

        // MeoFlowController routes
        app.get("/flows", ControllerMeoFlow::getAllFlows);
    }
}
