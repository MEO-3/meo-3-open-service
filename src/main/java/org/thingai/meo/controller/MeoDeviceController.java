package org.thingai.meo.controller;

import io.javalin.http.Context;

public class MeoDeviceController {
    public static void getAllDevices(Context ctx) {
        ctx.result("List of all devices");
    }
}
