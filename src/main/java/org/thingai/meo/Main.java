package org.thingai.meo;

import io.javalin.Javalin;
import org.thingai.meo.controller.Route;

public class Main {
    private static final MeoService meoService = MeoService.getInstance();
    private static final Javalin webService = Javalin.create();

    public static void main(String[] args) {
        MeoService.name = "MeoService";
        MeoService.appDirName = "meo_service";
        MeoService.version = "1.0.0";

        runApp();
    }

    public static void addRoutes() {
        Route routes = new Route(webService);
        routes.addRoutes();
    }

    public static void runApp() {
        meoService.init();
        meoService.run();

        addRoutes();
        webService.start(7000);
    }
}
