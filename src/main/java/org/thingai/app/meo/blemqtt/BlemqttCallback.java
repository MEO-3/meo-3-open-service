package org.thingai.app.meo.blemqtt;

@FunctionalInterface
public interface BlemqttCallback<T> {
    void handle(T value);
}
