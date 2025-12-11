package org.thingai.meo.entity;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "m_device")
public class MDevice {
    @DaoColumn(name = "eid", primaryKey = true)
    private int eid;

    @DaoColumn(name = "label")
    private String label;

    @DaoColumn(name = "model")
    private String model;

    @DaoColumn(name = "manufacturer")
    private String manufacturer;

    @DaoColumn(name = "type_connect")
    private int typeConnect;

    public MDevice(int eid, String label, String model, String manufacturer, int typeConnect) {
        this.eid = eid;
        this.label = label;
        this.model = model;
        this.manufacturer = manufacturer;
        this.typeConnect = typeConnect;
    }

    public MDevice() {

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getTypeConnect() {
        return typeConnect;
    }

    public void setTypeConnect(int typeConnect) {
        this.typeConnect = typeConnect;
    }
}