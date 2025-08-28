package org.thingai.meo.controller;

import io.javalin.http.Context;

public class ControllerMeoFlow {
    public static void getAllFlows(Context ctx) {
        ctx.result("List of all flows");
    }
}
