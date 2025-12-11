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
        app.get("/service/status", MServiceController::getServiceStatus);

        // MeoDeviceController routes
        app.get("/devices", MDeviceController::getAllDevices);
        app.get("/devices/<id:int>", MDeviceController::getDeviceById);
        app.delete("/devices/<id:int>", MDeviceController::deleteDeviceById);
        app.put("/devices/<id:int>", MDeviceController::updateDevice);
    }
}
