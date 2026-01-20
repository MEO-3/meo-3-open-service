package org.thingai.meo.common.entity.device;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "device_info")
public class MDeviceInfo {
    @DaoColumn(name = "device_id", primaryKey = true)
    private String deviceId;

    @DaoColumn(name = "compatible_app_version")
    private String compatibleAppVersion;

    @DaoColumn(name = "build_info")
    private String buildInfo;

    @DaoColumn(name = "mac_address")
    private String macAddress;

    @DaoColumn(name = "firmware_version")
    private String firmwareVersion;

    @DaoColumn(name = "device_type")
    private int deviceType;

    @DaoColumn(name = "connection_type")
    private int connectionType;

    @DaoColumn(name = "model")
    private String model;

    @DaoColumn(name = "product_id")
    private String productId;

    public MDeviceInfo() {

    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCompatibleAppVersion() {
        return compatibleAppVersion;
    }

    public void setCompatibleAppVersion(String compatibleAppVersion) {
        this.compatibleAppVersion = compatibleAppVersion;
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

    public String getBuildInfo() {
        return buildInfo;
    }

    public void setBuildInfo(String buildInfo) {
        this.buildInfo = buildInfo;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
