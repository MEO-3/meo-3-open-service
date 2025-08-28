package org.thingai.meo.controller;

import io.javalin.http.Context;

public class MeoFlowController {
    public static void getAllFlows(Context ctx) {
        ctx.result("List of all flows");
    }
}
