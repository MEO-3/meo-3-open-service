package org.thingai.meo.entity;

// This object hold in list when a device is discovered
public class MDeviceDiscoverInfo {
    private String ipAddress;
    private String macAddress;
    private int deviceType;
    private int connectionType;
    private String[] featureEvents;
    private String[] featureMethods;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
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

    public String[] getFeatureMethods() {
        return featureMethods;
    }

    public void setFeatureMethods(String[] featureMethods) {
        this.featureMethods = featureMethods;
    }
}
