package org.thingai.app.meo;

import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import org.thingai.base.log.ILog;
import org.thingai.app.meo.api.Route;

public class Main {
    private static final MeoService meoService = MeoService.getInstance();
    private static final Javalin webService = Javalin.create(config -> {
        config.bundledPlugins.enableCors(cors -> {
            cors.addRule(CorsPluginConfig.CorsRule::anyHost);
        });
    });

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
