package org.thingai.meo.common.entity.device;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "device")
public class MDevice {
    @DaoColumn(name = "id", primaryKey = true)
    private String id;

    @DaoColumn(name = "label")
    private String label;

    @DaoColumn(name = "product_id")
    private String productId;

    @DaoColumn(name = "user_id")
    private String userId;

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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}