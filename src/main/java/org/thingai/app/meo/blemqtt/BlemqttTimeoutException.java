package org.thingai.app.meo.blemqtt;

public class BlemqttTimeoutException extends RuntimeException {
    public BlemqttTimeoutException(String requestId) {
        super("blemqtt request timed out: " + requestId);
    }
}
