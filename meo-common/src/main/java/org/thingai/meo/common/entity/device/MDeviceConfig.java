package org.thingai.meo.common.entity.device;

public abstract class MDeviceConfig {
    protected String productId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
