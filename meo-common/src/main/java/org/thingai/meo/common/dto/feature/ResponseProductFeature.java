package org.thingai.meo.common.dto.feature;

public class ResponseProductFeature {
    private String productId;
    private int featureType;
    private int featureId;
    private String featureLabel;
    private String mdDocs;

    public ResponseProductFeature() {}

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getFeatureType() { return featureType; }
    public void setFeatureType(int featureType) { this.featureType = featureType; }

    public int getFeatureId() { return featureId; }
    public void setFeatureId(int featureId) { this.featureId = featureId; }

    public String getFeatureLabel() { return featureLabel; }
    public void setFeatureLabel(String featureLabel) { this.featureLabel = featureLabel; }

    public String getMdDocs() { return mdDocs; }
    public void setMdDocs(String mdDocs) { this.mdDocs = mdDocs; }
}
