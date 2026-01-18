package org.thingai.meo.common.dto.product;

public class ResponseProductRead {
    private String productId;
    private String productName;
    private String manufacturer;
    private int deviceType;
    private int connectionType;
    private String orgCode;

    public ResponseProductRead() {}

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public int getDeviceType() { return deviceType; }
    public void setDeviceType(int deviceType) { this.deviceType = deviceType; }

    public int getConnectionType() { return connectionType; }
    public void setConnectionType(int connectionType) { this.connectionType = connectionType; }

    public String getOrgCode() { return orgCode; }
    public void setOrgCode(String orgCode) { this.orgCode = orgCode; }
}
