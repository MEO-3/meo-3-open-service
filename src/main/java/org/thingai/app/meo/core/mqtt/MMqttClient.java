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

    public void connect() {
        try {
            ILog.d(TAG, "connect", mqttConfig.getBrokerUrl());
            client = new MqttClient(mqttConfig.getBrokerUrl(), mqttConfig.getClientId(), null);

            MqttConnectOptions options = getMqttConnectOptions();

            client.setCallback(mqttConfig.getMqttCallback());
            client.connect(options);
            ILog.i(TAG, "Connected to MQTT broker");

        } catch (MqttException e) {
            ILog.e(TAG, "Failed to connect MQTT: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                ILog.i(TAG, "Disconnected from MQTT broker");
            }
        } catch (MqttException e) {
            ILog.e(TAG, "Failed to disconnect MQTT: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }


    // synchronized methods for thread safety
    public synchronized void subscribe(String topic, int qos) {
        try {
            if (client != null && client.isConnected()) {
                client.subscribe(topic, qos);
                ILog.d(TAG, "Subscribed to topic: " + topic);
            } else {
                ILog.w(TAG, "Cannot subscribe, MQTT client is not connected");
            }
        } catch (MqttException e) {
            ILog.e(TAG, "Failed to subscribe to MQTT topic: " + e.getMessage());
        }
    }

    public synchronized void publish(String topic, byte[] payload, int qos, boolean retained) {
        try {
            if (client != null && client.isConnected()) {
                MqttMessage message = new MqttMessage(payload);
                message.setQos(qos);
                message.setRetained(retained);
                client.publish(topic, message);
                ILog.d(TAG, "Published message to topic: " + topic);
            } else {
                ILog.w(TAG, "Cannot publish message, MQTT client is not connected");
            }
        } catch (MqttException e) {
            ILog.e(TAG, "Failed to publish MQTT message: " + e.getMessage());
        }
    }

    public synchronized void publish(String topic, String payload, int qos, boolean retained) {
        publish(topic, payload.getBytes(), qos, retained);
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
