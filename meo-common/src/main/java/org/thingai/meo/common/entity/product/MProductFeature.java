package org.thingai.meo.common.entity.product;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "product_feature")
public class MProductFeature {
    @DaoColumn(name = "id", primaryKey = true)
    private int id; // = product_id (12 bytes) + featureId (2 bytes) (14 bytes)

    @DaoColumn(name = "product_id")
    private String productId;

    @DaoColumn(name = "feature_type")
    private int featureType;

    @DaoColumn(name = "feature_id")
    private int featureId; // unique id for each feature type in a product (from 0 to 65535) (2 bytes)

    @DaoColumn(name = "feature_label")
    private String featureLabel;

    @DaoColumn(name = "md_docs")
    private String mdDocs;

    public MProductFeature() {

    }

    public int getFeatureType() {
        return featureType;
    }

    public void setFeatureType(int featureType) {
        this.featureType = featureType;
    }

    public int getFeatureId() {
        return featureId;
    }

    public void setFeatureId(int featureId) {
        this.featureId = featureId;
    }

    public String getFeatureLabel() {
        return featureLabel;
    }

    public void setFeatureLabel(String featureLabel) {
        this.featureLabel = featureLabel;
    }

    public String getMdDocs() {
        return mdDocs;
    }

    public void setMdDocs(String mdDocs) {
        this.mdDocs = mdDocs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
