package org.thingai.meo.common.dto.auth;

import com.google.gson.annotations.SerializedName;

public class RequestSignup {
    private String username;
    private String email;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("auth_username")
    private String authUsername;
    private String password;

    public RequestSignup() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAuthUsername() { return authUsername; }
    public void setAuthUsername(String authUsername) { this.authUsername = authUsername; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
