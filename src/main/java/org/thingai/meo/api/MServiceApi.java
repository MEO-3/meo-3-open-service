package org.thingai.meo.api;

import io.javalin.http.Context;

public class MServiceApi {
    public static void getServiceStatus(Context ctx) {
        ctx.result("Meo Service is running");
    }
}
