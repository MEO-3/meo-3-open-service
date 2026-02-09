package org.thingai.meo.common.dto.device;

public class ResponseDeviceInfo {
    private String deviceId;
    private String label;
    private String productId;
    private String macAddress;
    private String compatibleAppVersion;
    private String firmwareVersion;
    private int deviceType;
    private int connectionType;
    private String model; // this field is for edge services only

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getCompatibleAppVersion() {
        return compatibleAppVersion;
    }

    public void setCompatibleAppVersion(String compatibleAppVersion) {
        this.compatibleAppVersion = compatibleAppVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(int connectionType) {
        this.connectionType = connectionType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
