package org.thingai.meo.common.entity.product;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "product")
public class MProduct {
    @DaoColumn(name = "product_id", primaryKey = true)
    private String productId;

    @DaoColumn(name = "product_name")
    private String productName;

    @DaoColumn(name = "manufacturer")
    private String manufacturer;

    @DaoColumn(name = "device_type")
    private int deviceType;

    @DaoColumn(name = "connection_type")
    private int connectionType;

    public MProduct() {

    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
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
}
