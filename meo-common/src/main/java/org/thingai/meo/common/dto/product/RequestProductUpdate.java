package org.thingai.meo.common.dto.product;

public class RequestProductUpdate {
    private String productName;
    private String manufacturer;
    private Integer deviceType;
    private Integer connectionType;

    public RequestProductUpdate() {}

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public Integer getDeviceType() { return deviceType; }
    public void setDeviceType(Integer deviceType) { this.deviceType = deviceType; }

    public Integer getConnectionType() { return connectionType; }
    public void setConnectionType(Integer connectionType) { this.connectionType = connectionType; }
}
