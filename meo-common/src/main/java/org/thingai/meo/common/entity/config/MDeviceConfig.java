package org.thingai.meo.common.entity.config;

// This object hold in list when a device is discovered
public abstract class MDeviceConfig {
    protected String macAddress;
    protected String manufacturer;
    protected String model;
    protected int connectionType;
    protected String[] featureEvents;
    protected String[] featureMethods;

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
}
