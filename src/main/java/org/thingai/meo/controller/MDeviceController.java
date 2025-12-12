package org.thingai.meo.controller;

import io.javalin.http.Context;
import org.thingai.meo.MeoService;
import org.thingai.meo.entity.MDeviceDiscoverInfo;

public class MDeviceController {
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

    // Discovery
    public static void getDiscoveredDevices(Context ctx) {
        MDeviceDiscoverInfo[] devices = MeoService.discoverHandler().getDevices();
        ctx.json(devices);
    }

    public static void registerDevice(Context ctx) {
        ctx.result("Device registered");
    }
}
