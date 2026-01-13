package org.thingai.app.meo.handler.mqtt;

import org.eclipse.paho.client.mqttv3.MqttCallback;

public class MMqttConfig {
    private String brokerUrl;
    private String clientId;
    private String username;
    private String password;

    private MqttCallback mqttCallback;

    public MMqttConfig(String brokerUrl, String clientId, String username, String password, MqttCallback mqttCallback) {
        this.brokerUrl = brokerUrl;
        this.username = username;
        this.password = password;
        this.mqttCallback = mqttCallback;

        if (clientId == null || clientId.isEmpty()) {
            this.clientId = "MeoClient_" + System.currentTimeMillis(); // handle null clientId with auto-generated id
        } else {
            this.clientId = clientId;
        }
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MqttCallback getMqttCallback() {
        return mqttCallback;
    }

    public void setMqttCallback(MqttCallback mqttCallback) {
        this.mqttCallback = mqttCallback;
    }
}
