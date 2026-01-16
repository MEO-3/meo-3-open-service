package org.thingai.meo.common.entity.device;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "device")
public class MDevice {
    @DaoColumn(name = "id", primaryKey = true)
    private String id;

    @DaoColumn(name = "label")
    private String label;

    @DaoColumn(name = "device_type")
    private int deviceType;

    public MDevice() {

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }
}