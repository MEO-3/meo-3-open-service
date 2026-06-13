package org.thingai.app.meo;

import io.javalin.Javalin;
import org.thingai.app.meo.api.Route;

public class Main {
    private static final String TAG = "Main";

    public static void main(String[] args) {
        MeoService meoService = new MeoService();
        meoService.init();

        Javalin.create(config -> {
            new Route(config, meoService.getDeviceHandler()).addRoutes();
        }).start(getPort());
    }

    private static int getPort() {
        String port = System.getenv("MEO_SERVICE_PORT");
        if (port == null || port.trim().isEmpty()) {
            return 7070;
        }
        return Integer.parseInt(port);
    }
}
