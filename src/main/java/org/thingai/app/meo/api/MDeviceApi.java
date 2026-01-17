package org.thingai.app.meo.api;

import com.google.gson.JsonObject;
import io.javalin.http.Context;
import org.thingai.app.meo.util.JsonUtil;
import org.thingai.base.log.ILog;
import org.thingai.app.meo.MeoService;
import org.thingai.meo.common.callback.RequestCallback;
import org.thingai.meo.common.entity.device.MDevice;
import org.thingai.meo.common.entity.device.MDeviceConfigLan;

public class MDeviceApi {
    // General
    public static void getAllDevices(Context ctx) {
        MDevice[] devices = MeoService.deviceManager().getAllDevices();
        ctx.json(devices);
        ctx.status(200);
    }

    public static void getDeviceById(Context ctx) {
        String deviceId = ctx.pathParam("id");
        MDevice device = MeoService.deviceManager().getDevice(deviceId);
        ctx.json(device);
        ctx.status(200);
    }

    public static void deleteDeviceById(Context ctx) {
        String deviceId = ctx.pathParam("id");
        MeoService.deviceManager().deleteDevice(deviceId);

        String jsonResponse = "{\"message\": \"Deleted device with ID: " + deviceId + "\"}";

        ctx.json(jsonResponse);
        ctx.status(200);
    }

    public static void updateDevice(Context ctx) {
        String deviceId = ctx.pathParam("id");
        String body = ctx.body();
        JsonObject bodyJson = JsonUtil.fromJson(body, JsonObject.class);
        String newLabel = bodyJson.get("label").getAsString();
        MeoService.deviceManager().updateDeviceLabel(deviceId, newLabel);
        ctx.result("Updated device label for ID: " + deviceId);
        ctx.status(200);
    }
}
