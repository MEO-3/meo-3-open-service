package org.thingai.app.meo;

import io.javalin.Javalin;
import org.thingai.app.meo.api.Route;
import org.thingai.app.meo.blemqtt.BlemqttClient;
import org.thingai.app.meo.blemqtt.BlemqttCommand;
import org.thingai.app.meo.blemqtt.BlemqttConfig;
import org.thingai.app.meo.blemqtt.BlemqttOp;
import org.thingai.base.log.ILog;

public class Main {
    private static final String TAG = "Main";

    public static void main(String[] args) {
        MeoService meoService = new MeoService();
        meoService.init();

        var app = Javalin.create(config -> {
            new Route(config).addRoutes();
        }).start(7070);
    }

//    private static void sampleBlemqttCall() {
//        BlemqttClient blemqttClient = new BlemqttClient(new BlemqttConfig());
//
//        try {
//            blemqttClient.connect();
//            BlemqttCommand command = BlemqttCommand.create(BlemqttOp.ADAPTER_STATUS);
//            blemqttClient.send(command).thenAccept(reply -> {
//                if (reply.isOk()) {
//                    ILog.i(TAG, "blemqtt adapter.status", reply.getResult());
//                } else if (reply.getError() != null) {
//                    ILog.w(TAG, "blemqtt adapter.status failed", reply.getError().getMessage());
//                }
//            }).exceptionally(error -> {
//                ILog.e(TAG, "blemqtt adapter.status error", error);
//                return null;
//            });
//            blemqttClient.send(BlemqttCommand.create(BlemqttOp.SCAN_START)).thenAccept(reply -> {
//                if (reply.isOk()) {
//                    ILog.i(TAG, "scan.start.status", reply.getResult());
//                }
//            });
//        } catch (Exception e) {
//            ILog.e(TAG, "blemqtt sample failed", e);
//        }
//    }
}
