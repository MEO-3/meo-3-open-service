package org.thingai.app.meo;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.thingai.app.meo.core.mqtt.MMqttConfig;
import org.thingai.app.meo.handler.device.MDeviceConfigLanHandler;
import org.thingai.app.meo.core.mqtt.MMqttClient;
import org.thingai.app.meo.handler.telemetry.MTelemetryHandler;
import org.thingai.app.meo.service.MeoDiscoverServiceLan;
import org.thingai.base.Service;
import org.thingai.base.dao.Dao;
import org.thingai.meo.common.entity.feature.MDeviceFeatureEvent;
import org.thingai.meo.common.entity.feature.MDeviceFeatureMethod;
import org.thingai.platform.dao.DaoFile;
import org.thingai.platform.dao.DaoSqlite;
import org.thingai.base.log.ILog;
import org.thingai.meo.common.entity.MDevice;
import org.thingai.app.meo.handler.device.MDeviceFeatureHandler;
import org.thingai.app.meo.handler.device.MDeviceHandler;
import org.thingai.app.meo.handler.MServiceHandler;

public class MeoService extends Service {
    private static final String TAG = "MeoService";
    private static final MeoService instance = new MeoService();

    private static MServiceHandler serviceHandler;
    private static MDeviceHandler deviceManager;
    private static MDeviceConfigLanHandler discoverHandler;
    private static MDeviceFeatureHandler featureHandler;
    private static MTelemetryHandler telemetryHandler;

    private MeoService() {
    }

    public static MeoService getInstance() {
        return instance;
    }

    @Override
    protected void onServiceInit() {
        // init dao
        Dao dao = new DaoSqlite(appDir + "/meo.db");
        DaoFile daoFile = new DaoFile(appDir + "/data");

        ILog.d("MeoService", "SQLite DAO initialized at: " + appDir + "/meo.db");
        ILog.d("MeoService", "DAO File initialized at: " + appDir + "/data");

        dao.initDao(new Class[]{
            MDevice.class,
            MDeviceFeatureEvent.class,
            MDeviceFeatureMethod.class
        });

        // init handlers
        featureHandler = new MDeviceFeatureHandler(dao);
        deviceManager = new MDeviceHandler(dao, featureHandler);
        serviceHandler = new MServiceHandler();
        discoverHandler = new MDeviceConfigLanHandler(10, deviceManager);
        telemetryHandler = new MTelemetryHandler();

        // start device discovery service
        MeoDiscoverServiceLan discoveryService = new MeoDiscoverServiceLan(8901, discoverHandler);
        Thread discoveryThread = new Thread(discoveryService);
        discoveryThread.setDaemon(true);
        discoveryThread.start();

        // init MQTT client
        MMqttClient mqttClient = new MMqttClient(new MMqttConfig(
                "tcp://localhost:1883",
                null,
                null,
                null,
                new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        ILog.w(TAG, "MQTT connection lost: " + (cause != null ? cause.getMessage() : "unknown"));
                        // Automatic reconnect is enabled in options
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) {
                        String payload = new String(message.getPayload());
                        ILog.d(TAG, "MQTT message arrived on " + topic + ": " + payload);
                        // Handle topic arrive with telemetry handler
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        // Optional logging
                    }
                }));
        mqttClient.connect();
    }

    public static MServiceHandler serviceHandler() {
        return serviceHandler;
    }

    public static MDeviceHandler deviceManager() {
        return deviceManager;
    }

    public static MDeviceConfigLanHandler discoverHandler() {
        return discoverHandler;
    }

    public static MDeviceFeatureHandler featureHandler() {
        return featureHandler;
    }
}
