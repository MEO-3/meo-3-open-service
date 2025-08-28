package org.thingai.meo.controller;

import io.javalin.http.Context;
import org.thingai.meo.MeoService;
import org.thingai.meo.entities.MeoDeviceInfo;
import org.thingai.meo.handlers.MeoHandlerDevice;

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
    public static void getScannedLanDevice(Context ctx) {
        MeoService.deviceHandler().scanLanDevice(new MeoHandlerDevice.MeoDeviceScanCallback() {
            @Override
            public void onDeviceFound(MeoDeviceInfo deviceInfo, String message) {
                System.out.println(deviceInfo.getMacAddress());
            }

            @Override
            public void onError(String errorMessage) {
                ctx.status(500).result("Error during scan: " + errorMessage);
            }
        }, 1);
        // TODO(Fix this to return actual scanned devices, maybe use async handling)
    }

    public static void getScannedBleDevice(Context ctx) {
        ctx.result("List of scanned BLE devices");
    }
}
