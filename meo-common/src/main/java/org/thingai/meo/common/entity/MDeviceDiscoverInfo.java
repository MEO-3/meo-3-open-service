package org.thingai.meo.common.entity;

// This object hold in list when a device is discovered
public class MDeviceDiscoverInfo {
    private String ipAddress;
    private String macAddress;
    private String manufacturer;
    private String model;
    private int connectionType;
    private String[] featureEvents;
    private String[] featureMethods;

    // LAN device
    private int listeningPort;

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

    public String[] getFeatureEvents() {
        return featureEvents;
    }

    public void setFeatureEvents(String[] featureEvents) {
        this.featureEvents = featureEvents;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public void setListeningPort(int listeningPort) {
        this.listeningPort = listeningPort;
    }
}
