package org.thingai.app.meo.blemqtt;

public enum BlemqttOp {
    ADAPTER_POWER("adapter.power"),
    ADAPTER_STATUS("adapter.status"),
    SCAN_START("scan.start"),
    SCAN_STOP("scan.stop"),
    DEVICE_LIST("device.list"),
    DEVICE_CONNECT("device.connect"),
    DEVICE_DISCONNECT("device.disconnect"),
    GATT_SERVICES("gatt.services"),
    GATT_READ("gatt.read"),
    GATT_WRITE("gatt.write"),
    GATT_SUBSCRIBE("gatt.subscribe"),
    GATT_UNSUBSCRIBE("gatt.unsubscribe"),
    ADVERTISE_START("advertise.start"),
    ADVERTISE_STOP("advertise.stop");

    private final String value;

    BlemqttOp(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
