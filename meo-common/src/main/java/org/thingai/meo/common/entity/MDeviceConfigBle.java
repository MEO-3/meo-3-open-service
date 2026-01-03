package org.thingai.meo.common.entity;

import java.util.HashMap;

public class MDeviceConfigBle extends MDeviceConfig {
    private String bleAddress;
    private String serviceUuid;
    private HashMap<String, String> characteristicUuids; // key: characteristic name, value: UUID

    public String getBleAddress() {
        return bleAddress;
    }

    public void setBleAddress(String bleAddress) {
        this.bleAddress = bleAddress;
    }

    public String getServiceUuid() {
        return serviceUuid;
    }

    public void setServiceUuid(String serviceUuid) {
        this.serviceUuid = serviceUuid;
    }

    public HashMap<String, String> getCharacteristicUuids() {
        return characteristicUuids;
    }

    public void setCharacteristicUuids(HashMap<String, String> characteristicUuids) {
        this.characteristicUuids = characteristicUuids;
    }
}
