package org.thingai.app.meo.api;

import io.javalin.config.JavalinConfig;

public class Route {
    private final JavalinConfig config;

    public Route(JavalinConfig config) {
        this.config = config;
    }

    public void addRoutes() {
        config.routes.get("/", ctx -> {
            ctx.json("meow");
        });
    }
}
