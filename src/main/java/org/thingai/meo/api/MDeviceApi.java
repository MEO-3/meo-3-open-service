package org.thingai.meo.api;

import com.google.gson.JsonObject;
import io.javalin.http.Context;
import org.thingai.base.log.ILog;
import org.thingai.meo.MeoService;
import org.thingai.meo.callback.MRequestCallback;
import org.thingai.meo.entity.MDevice;
import org.thingai.meo.entity.MDeviceDiscoverInfo;

public class MDeviceApi {
    // General
    public static void getAllDevices(Context ctx) {
        MDevice[] devices = MeoService.deviceManager().getAllDevices();
        ctx.json(devices);
    }

    public static void getDeviceById(Context ctx) {
        String deviceId = ctx.pathParam("id");
        MDevice device = MeoService.deviceManager().getDevice(deviceId);
        ctx.json(device);
    }

    public static void deleteDeviceById(Context ctx) {
        String deviceId = ctx.pathParam("id");
        MeoService.deviceManager().deleteDevice(deviceId);

        String jsonResponse = "{\"message\": \"Deleted device with ID: " + deviceId + "\"}";

        ctx.json(jsonResponse);
    }

    public static void updateDevice(Context ctx) {
        String deviceId = ctx.pathParam("id");
        String body = ctx.body();
        JsonObject bodyJson = MeoService.getGson().fromJson(body, JsonObject.class);
        String newLabel = bodyJson.get("label").getAsString();
        MeoService.deviceManager().updateDeviceLabel(deviceId, newLabel);
        ctx.result("Updated device label for ID: " + deviceId);
    }

    // Discovery
    public static void getDiscoveredDevices(Context ctx) {
        MDeviceDiscoverInfo[] devices = MeoService.discoverHandler().getDiscoveredDeviceInfo();
        ctx.json(devices);
    }

    public static void registerDevice(Context ctx) {
        String body = ctx.body();

        ILog.d("MDeviceController", "Register device request body: " + body);

        JsonObject bodyJson = MeoService.getGson().fromJson(body, JsonObject.class);
        int index = bodyJson.get("index").getAsInt();
        String label = bodyJson.get("label").getAsString();

        MeoService.discoverHandler().registerDevice(index, label, new MRequestCallback<MDevice>() {
            @Override
            public void onSuccess(MDevice result, String message) {
                ILog.d("MDeviceController", "Device registered: " + MeoService.getGson().toJson(result));
                ctx.json(result);
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                ILog.e("MDeviceController", "Device registration failed: " + errorMessage);
                ctx.status(500).result("Device registration failed: " + errorMessage);
            }
        });
    }
}
