package org.thingai.app.meo.core.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.jetbrains.annotations.NotNull;
import org.thingai.base.log.ILog;

public class MMqttClient {
    private static final String TAG = "MMqttHandler";

    private MqttClient client;
    private final MMqttConfig mqttConfig;

    public MMqttClient(MMqttConfig config) {
        this.mqttConfig = config;
    }

    public void connectAndSubscribe() {
        try {
            ILog.d(TAG, "connectAndSubscribe", mqttConfig.getBrokerUrl());
            client = new MqttClient(mqttConfig.getBrokerUrl(), mqttConfig.getClientId(), null);

            MqttConnectOptions options = getMqttConnectOptions();

            client.setCallback(mqttConfig.getMqttCallback());
            client.connect(options);
            ILog.i(TAG, "Connected to MQTT broker");

        } catch (MqttException e) {
            ILog.e(TAG, "Failed to connect MQTT: " + e.getMessage());
        }
    }

    public MMqttConfig getMqttConfig() {
        return mqttConfig;
    }

    @NotNull
    private MqttConnectOptions getMqttConnectOptions() {
        String username = mqttConfig.getUsername();
        String password = mqttConfig.getPassword();

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        if (username != null && !username.isEmpty()) {
            options.setUserName(username);
        }
        if (password != null && !password.isEmpty()) {
            options.setPassword(password.toCharArray());
        }
        return options;
    }

}
