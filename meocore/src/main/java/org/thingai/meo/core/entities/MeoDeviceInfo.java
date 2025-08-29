package org.thingai.meo.core.entities;

public class MeoDeviceInfo {
    // common attributes
    private String macAddress;
    private String model;
    private String manufacturer;
    private String firmwareVersion;
    private int typeConnect; // 0: LAN, 1: BLE

    // LAN specific attributes
    private String ipAddress;

    // TODO(BLE specific attributes)
    // TODO(UART specific attributes)

    public MeoDeviceInfo(String macAddress, String model, String manufacturer, String firmwareVersion, int typeConnect, String ipAddress) {
        this.macAddress = macAddress;
        this.model = model;
        this.manufacturer = manufacturer;
        this.firmwareVersion = firmwareVersion;
        this.typeConnect = typeConnect;
        this.ipAddress = ipAddress;
    }

    public MeoDeviceInfo() {

    }

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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    // TODO(BLE specific attributes)
}
