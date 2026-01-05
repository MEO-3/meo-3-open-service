package org.thingai.app.meo.core.mqtt;

import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.*;
import org.jetbrains.annotations.NotNull;
import org.thingai.app.meo.util.JsonUtil;
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

            client.setCallback(new _MqttCallback());
            client.connect(options);
            ILog.i(TAG, "Connected to MQTT broker");

            // Subscribe to all device events: meo/+/event
            client.subscribe("meo/+/event/+");
            ILog.i(TAG, "Subscribed to topic: meo/+/event/+");
        } catch (MqttException e) {
            ILog.e(TAG, "Failed to connect/subscribe MQTT: " + e.getMessage());
        }
    }

    public MMqttConfig getMqttConfig() {
        return mqttConfig;
    }

    private class _MqttCallback implements MqttCallback {
        @Override
        public void connectionLost(Throwable cause) {
            ILog.w(TAG, "MQTT connection lost: " + (cause != null ? cause.getMessage() : "unknown"));
            // Automatic reconnect is enabled in options
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            String payload = new String(message.getPayload());
            ILog.d(TAG, "MQTT message arrived on " + topic + ": " + payload);

            // Expect topic: meo/{deviceId}/event/{eventName}
            String[] parts = topic.split("/");
            if (parts.length != 4 || !"meo".equals(parts[0]) || !"event".equals(parts[2])) {
                ILog.w(TAG, "Unexpected topic format: " + topic);
                return;
            }

            String deviceId = parts[1];

            try {
                JsonObject json = JsonUtil.fromJson(payload, JsonObject.class);
                if (json == null) {
                    ILog.w(TAG, "Invalid JSON from device " + deviceId);
                    return;
                }

                String eventName = parts[3];

                if ("feature_response".equals(eventName)) {
                    ILog.d(TAG, "Feature response from " + deviceId + ": " + payload);
                    return;
                }

                // Normal telemetry event
                if (telemetryHandler != null) {
                    telemetryHandler.handleDeviceEvent(deviceId, eventName, json);
                }
            } catch (Exception e) {
                ILog.e(TAG, "Failed to handle MQTT message: " + e.getMessage());
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            // Optional logging
        }
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
