package org.thingai.app.meo.entity;

public class MeoDeviceProvision {
    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_SCANNING = "SCANNING";
    public static final String STATUS_CONNECTING_BLE = "CONNECTING_BLE";
    public static final String STATUS_CONNECTED_BLE = "CONNECTED_BLE";
    public static final String STATUS_READING_MAC = "READING_MAC";
    public static final String STATUS_READING_PRODUCT_ID = "READING_PRODUCT_ID";
    public static final String STATUS_WRITING_WIFI = "WRITING_WIFI";
    public static final String STATUS_READING_STATUS = "READING_STATUS";
    public static final String STATUS_DISCONNECTING_BLE = "DISCONNECTING_BLE";
    public static final String STATUS_DISCONNECTED_BLE = "DISCONNECTED_BLE";
    public static final String STATUS_FAILED = "FAILED";

    private String bleAddress;
    private String macAddress;
    private String productId;
    private String wifiSsid;
    private String provisionStatus;
    private String status = STATUS_CREATED;
    private String message;

    public String getBleAddress() {
        return bleAddress;
    }

    public void setBleAddress(String bleAddress) {
        this.bleAddress = bleAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getWifiSsid() {
        return wifiSsid;
    }

    public void setWifiSsid(String wifiSsid) {
        this.wifiSsid = wifiSsid;
    }

    public String getProvisionStatus() {
        return provisionStatus;
    }

    public void setProvisionStatus(String provisionStatus) {
        this.provisionStatus = provisionStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
