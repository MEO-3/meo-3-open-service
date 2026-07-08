package org.thingai.app.meo.api;

import io.javalin.config.JavalinConfig;
import org.thingai.app.meo.handler.MeoDeviceHandler;

public class Route {
    private final JavalinConfig config;
    private final MeoDeviceHandler deviceHandler;

    public Route(JavalinConfig config, MeoDeviceHandler deviceHandler) {
        this.config = config;
        this.deviceHandler = deviceHandler;
    }

    public void addRoutes() {
        config.routes.get("/", ctx -> {
            ctx.json("meow");
        });
    }
}
