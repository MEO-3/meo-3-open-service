package org.thingai.meo.common.dto.system;

public class ResponseOrgCreate {
    private String orgCode;
    private String orgName;
    private String adminUserId;

    public ResponseOrgCreate() {}

    public String getOrgCode() { return orgCode; }
    public void setOrgCode(String orgCode) { this.orgCode = orgCode; }

    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }

    public String getAdminUserId() { return adminUserId; }
    public void setAdminUserId(String adminUserId) { this.adminUserId = adminUserId; }
}
