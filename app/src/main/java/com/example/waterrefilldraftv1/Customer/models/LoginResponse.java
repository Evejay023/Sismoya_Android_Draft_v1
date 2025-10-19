package com.example.waterrefilldraftv1.Customer.models;

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

    // Constructor
    public LoginResponse(boolean error, String message, User user, String token) {
        this.error = error;
        this.message = message;
        this.user = user;
        this.token = token;
    }

    // Getters
    public boolean isError() { return error; }
    public boolean isSuccess() { return !error; }
    public String getMessage() { return message; }
    public User getUser() { return user; }
    public String getToken() { return token; }

    // Setters
    public void setError(boolean error) { this.error = error; }
    public void setMessage(String message) { this.message = message; }
    public void setUser(User user) { this.user = user; }
    public void setToken(String token) { this.token = token; }
}
