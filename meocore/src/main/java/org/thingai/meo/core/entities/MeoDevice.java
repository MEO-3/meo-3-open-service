package org.thingai.meo.core.entities;

public class MeoDevice {
    private int eid;
    private String label;
    private String model;
    private String manufacturer;
    private int typeConnect;

    public MeoDevice(int eid, String label, String model, String manufacturer) {
        this.eid = eid;
        this.label = label;
        this.model = model;
        this.manufacturer = manufacturer;
    }

    public MeoDevice() {

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
}