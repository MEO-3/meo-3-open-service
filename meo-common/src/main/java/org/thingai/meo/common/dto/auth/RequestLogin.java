package org.thingai.meo.common.dto.auth;

import com.google.gson.annotations.SerializedName;

public class RequestLogin {
    @SerializedName("auth_username")
    private String authUsername;
    private String password;

    public RequestLogin() {}

    public String getAuthUsername() { return authUsername; }
    public void setAuthUsername(String authUsername) { this.authUsername = authUsername; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
