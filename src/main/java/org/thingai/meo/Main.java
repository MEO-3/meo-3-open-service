package org.thingai.meo;

import io.javalin.Javalin;
import org.thingai.base.log.ILog;
import org.thingai.meo.api.Route;

public class Main {
    private static final MeoService meoService = MeoService.getInstance();
    private static final Javalin webService = Javalin.create();

    public static void main(String[] args) {
        meoService.name = "meoService";
        meoService.appDirName = "meo_service";
        meoService.version = "1.0.0";

        ILog.ENABLE_LOGGING = true;
        ILog.logLevel = ILog.DEBUG;

        runApp();
    }

    public static void addRoutes() {
        Route routes = new Route(webService);
        routes.addRoutes();
    }

    public static void runApp() {
        meoService.init();

        addRoutes();
        webService.start(7000);
    }
}
