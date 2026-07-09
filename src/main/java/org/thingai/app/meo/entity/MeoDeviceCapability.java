package org.thingai.app.meo.entity;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

// One row per capability a device reports during provisioning. Capability ids
// are stored verbatim (unknown ids are kept, resolved to labels at the display
// edge). On re-provision the device's rows are replaced wholesale.
@DaoTable(name = "meo_device_capabilities", version = 1)
public class MeoDeviceCapability {
    @DaoColumn(primaryKey = true, autoIncrement = true)
    private int id;
    @DaoColumn(nullable = false)
    private String deviceId;
    @DaoColumn(nullable = false)
    private int capabilityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(int capabilityId) {
        this.capabilityId = capabilityId;
    }
}
