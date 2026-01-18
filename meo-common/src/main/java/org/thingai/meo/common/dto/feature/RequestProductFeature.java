package org.thingai.meo.common.dto.feature;

public class RequestProductFeature {
    private Integer featureType;
    private Integer featureId;
    private String featureLabel;
    private String mdDocs;

    public RequestProductFeature() {}

    public Integer getFeatureType() { return featureType; }
    public void setFeatureType(Integer featureType) { this.featureType = featureType; }

    public Integer getFeatureId() { return featureId; }
    public void setFeatureId(Integer featureId) { this.featureId = featureId; }

    public String getFeatureLabel() { return featureLabel; }
    public void setFeatureLabel(String featureLabel) { this.featureLabel = featureLabel; }

    public String getMdDocs() { return mdDocs; }
    public void setMdDocs(String mdDocs) { this.mdDocs = mdDocs; }
}
