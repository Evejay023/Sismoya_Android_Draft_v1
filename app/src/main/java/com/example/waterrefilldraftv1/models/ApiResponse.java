package com.example.waterrefilldraftv1.models;

import com.google.gson.annotations.SerializedName;

/**
 * Generic API response wrapper.
 * Some APIs return {"error": false, "message": "..."}
 * others return {"success": true, "message": "..."}.
 * This class handles both.
 */
public class ApiResponse {
    @SerializedName("error")
    private Boolean error;

    @SerializedName("success")
    private Boolean success;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        if (success != null) return success;
        if (error != null) return !error;
        return false;
    }

    public String getMessage() { return message; }

    // Optional setters
    public void setError(Boolean error) { this.error = error; }
    public void setSuccess(Boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
}
