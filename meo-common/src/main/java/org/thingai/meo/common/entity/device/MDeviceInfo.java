package org.thingai.meo.common.entity.device;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "device_info")
public class MDeviceInfo {
    @DaoColumn(name = "device_id", primaryKey = true)
    private String deviceId;

    @DaoColumn(name = "compatible_app_version")
    private String compatibleAppVersion;

    @DaoColumn(name = "build_number")
    private String buildNumber;

    @DaoColumn(name = "firmware_version")
    private String firmwareVersion;

    @DaoColumn(name = "mac_address")
    private String macAddress;

    @DaoColumn(name = "device_type")
    private String deviceType;

    @DaoColumn(name = "connection_type")
    private String connectionType;

    @DaoColumn(name = "model")
    private String model;

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

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
}
