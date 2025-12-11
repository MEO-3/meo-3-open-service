package org.thingai.meo.controller;

import io.javalin.http.Context;

public class MServiceController {
    public static void getServiceStatus(Context ctx) {
        ctx.result("Meo Service is running");
    }
}
