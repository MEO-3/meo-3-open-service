package org.thingai.meo.common.dto.system;

public class RequestOrgCreate {
    private String orgCode;
    private String orgName;
    private String userId;

    public RequestOrgCreate() {}

    public String getOrgCode() { return orgCode; }
    public void setOrgCode(String orgCode) { this.orgCode = orgCode; }

    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
