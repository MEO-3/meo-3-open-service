package org.thingai.meo.entities;

public class MeoDeviceInfo {
    // common attributes
    private String macAddress;
    private String model;
    private String manufacturer;
    private String firmwareVersion;
    private int typeConnect; // 0: LAN, 1: BLE

    // LAN specific attributes
    private String ipAddress;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public int getTypeConnect() {
        return typeConnect;
    }

    public void setTypeConnect(int typeConnect) {
        this.typeConnect = typeConnect;
    }

    // TODO(BLE specific attributes)
}
