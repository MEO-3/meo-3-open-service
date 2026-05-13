package org.thingai.app.meo;

import io.javalin.Javalin;
import org.thingai.app.meo.api.Route;

public class Main {
    public static void main(String[] args) {
        MeoService meoService = new MeoService();
        meoService.init();

        var app = Javalin.create(config -> {
            new Route(config).addRoutes();
        }).start(7070);
    }
}
