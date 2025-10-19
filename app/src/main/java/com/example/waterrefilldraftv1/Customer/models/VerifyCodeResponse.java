package com.example.waterrefilldraftv1.Customer.models;

public class VerifyCodeResponse {
    private boolean success;
    private String message;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
}
