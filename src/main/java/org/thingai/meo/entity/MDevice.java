package org.thingai.meo.entity;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "device")
public class MDevice {
    @DaoColumn(name = "id", primaryKey = true)
    private String id;

    @DaoColumn(name = "label")
    private String label;

    @DaoColumn(name = "model")
    private String model;

    @DaoColumn(name = "manufacturer")
    private String manufacturer;

    @DaoColumn(name = "connection_type")
    private int connectionType;

    public MDevice(String id, String label, String model, String manufacturer, int connectionType) {
        this.id = id;
        this.label = label;
        this.model = model;
        this.manufacturer = manufacturer;
        this.connectionType = connectionType;
    }

    public MDevice() {

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public int getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(int connectionType) {
        this.connectionType = connectionType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}