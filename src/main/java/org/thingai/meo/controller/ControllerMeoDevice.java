package org.thingai.meo.controller;

import io.javalin.http.Context;

public class ControllerMeoDevice {
    // General
    public static void getAllDevices(Context ctx) {
        ctx.result("List of all devices");
    }

    public static void getDeviceById(Context ctx) {
        String deviceId = ctx.pathParam("id");
        ctx.result("Details of device with ID: " + deviceId);
    }

    public static void deleteDeviceById(Context ctx) {
        String deviceId = ctx.pathParam("id");
        ctx.result("Deleted device with ID: " + deviceId);
    }

    public static void updateDevice(Context ctx) {
        String deviceId = ctx.pathParam("id");
        ctx.result("Updated device with ID: " + deviceId);
    }

    // IoT Specific
    public static void getScanDevice(Context ctx) {
        ctx.result("Scanning for new devices");
    }
}
