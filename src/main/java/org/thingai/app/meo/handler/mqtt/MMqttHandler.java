package org.thingai.app.meo.handler.mqtt;

import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.*;
import org.thingai.app.meo.handler.telemetry.MTelemetryHandler;
import org.thingai.app.meo.util.JsonUtil;
import org.thingai.base.log.ILog;

public class MMqttHandler {
    private static final String TAG = "MMqttHandler";

    private final String brokerUrl;
    private final String clientId;
    private final String username;   // optional: use for auth
    private final String password;   // optional: use for auth

    private MqttClient client;
    private MTelemetryHandler telemetryHandler;

    public MMqttHandler(String brokerUrl, String clientId, String username, String password) {
        this.brokerUrl = brokerUrl;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
    }

    public void setTelemetryHandler(MTelemetryHandler telemetryHandler) {
        this.telemetryHandler = telemetryHandler;
    }

    public void connectAndSubscribe() {
        try {
            ILog.d(TAG, "Connecting to MQTT broker: " + brokerUrl);
            client = new MqttClient(brokerUrl, clientId, null);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            if (username != null && !username.isEmpty()) {
                options.setUserName(username);
            }
            if (password != null && !password.isEmpty()) {
                options.setPassword(password.toCharArray());
            }

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

    // Publish feature invocation to device
    public void publishFeatureInvoke(String deviceId, String featureName, JsonObject params) {
        if (client == null || !client.isConnected()) {
            ILog.e(TAG, "MQTT client not connected; cannot publish feature invoke");
            return;
        }

        String topic = String.format("meo/%s/feature/%s/invoke", deviceId, featureName);

        JsonObject payload = new JsonObject();
        // Optionally generate a request_id here
        payload.addProperty("request_id", "req-" + System.currentTimeMillis());
        if (params != null) {
            payload.add("params", params);
        }

        String json = JsonUtil.toJson(payload);

        try {
            ILog.d(TAG, "Publishing feature invoke to " + topic + ": " + json);
            client.publish(topic, new MqttMessage(json.getBytes()));
        } catch (MqttException e) {
            ILog.e(TAG, "Failed to publish feature invoke: " + e.getMessage());
        }
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

}
