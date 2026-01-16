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
}
