package com.example.waterrefilldraftv1.Login_Customer_and_Riders;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("identifier")
    private String identifier;

    @SerializedName("password")
    private String password;

    public LoginRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier() { return identifier; }
    public String getPassword() { return password; }
}
