package com.example.waterrefilldraftv1.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user;

    @SerializedName("token")
    private String token;

    public LoginResponse(boolean error, String message, User user, String token) {
        this.error = error;
        this.message = message;
        this.user = user;
        this.token = token;
    }

    public LoginResponse(boolean error, String message, User user) {
        this.error = error;
        this.message = message;
        this.user = user;
    }

    public boolean isError() { return error; }

    // ✅ Add this
    public boolean isSuccess() { return !error; }

    public String getMessage() { return message; }
    public User getUser() { return user; }
    public String getToken() { return token; }
}
