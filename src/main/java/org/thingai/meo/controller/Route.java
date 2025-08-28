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
        app.get("/devices/<id:int>", ControllerMeoDevice::getDeviceById);
        app.delete("/devices/<id:int>", ControllerMeoDevice::deleteDeviceById);
        app.put("/devices/<id:int>", ControllerMeoDevice::updateDevice);

        app.get("/devices/scan/lan", ControllerMeoDevice::getScannedLanDevice);
        app.get("/devices/scan/ble", ControllerMeoDevice::getScannedBleDevice);

        // MeoFlowController routes
        app.get("/flows", ControllerMeoFlow::getAllFlows);
    }
}
