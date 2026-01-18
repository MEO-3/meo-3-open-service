package org.thingai.meo.common.dto.auth;

import com.google.gson.annotations.SerializedName;

public class RequestRefresh {
    @SerializedName("refresh_token")
    private String refreshToken;

    public RequestRefresh() {}

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
