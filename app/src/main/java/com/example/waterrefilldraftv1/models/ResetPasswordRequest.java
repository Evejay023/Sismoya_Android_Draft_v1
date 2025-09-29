package com.example.waterrefilldraftv1.models;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("code")
    private String code;

    @SerializedName("password")
    private String password;

    @SerializedName("confirm_password")
    private String confirmPassword;

    public ResetPasswordRequest(String email, String code, String password, String confirmPassword) {
        this.email = email;
        this.code = code;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() { return email; }
    public String getCode() { return code; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }
}
