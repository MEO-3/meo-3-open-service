package org.thingai.meo.api;

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
        app.get("/api/service/status", MServiceApi::getServiceStatus);

        // MeoDeviceController routes
        app.get("/api/devices", MDeviceApi::getAllDevices);
        app.get("/api/devices/{id}", MDeviceApi::getDeviceById);
        app.delete("/api/devices/{id}", MDeviceApi::deleteDeviceById);
        app.put("/api/devices/{id}", MDeviceApi::updateDevice);

        // Discovery routes
        app.get("/api/discover/list", MDeviceApi::getDiscoveredDevices);
        app.post("/api/discover/register", MDeviceApi::registerDevice);
    }
}
