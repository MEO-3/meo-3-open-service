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
        app.get("/api/service/status", MServiceController::getServiceStatus);

        // MeoDeviceController routes
        app.get("/api/devices", MDeviceController::getAllDevices);
        app.get("/api/devices/<id:int>", MDeviceController::getDeviceById);
        app.delete("/api/devices/<id:int>", MDeviceController::deleteDeviceById);
        app.put("/api/devices/<id:int>", MDeviceController::updateDevice);

        // Discovery routes
        app.get("/api/discover/list", MDeviceController::getDiscoveredDevices);
        app.post("/api/discover/register", MDeviceController::registerDevice);
    }
}
