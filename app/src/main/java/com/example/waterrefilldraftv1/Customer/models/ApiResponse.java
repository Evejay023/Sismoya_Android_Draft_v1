package com.example.waterrefilldraftv1.Customer.models;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    @SerializedName("error")
    private Boolean error;

    @SerializedName("success")
    private Boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("address_id")
    private Integer addressId;

    // Sometimes backend sends user data as "user" or "data"
    @SerializedName("user")
    private User user;

    @SerializedName("data")
    private Object data;

    // ✅ Returns user from whichever field exists
    public User getUser() {
        if (user != null) return user;
        if (data instanceof User) return (User) data;
        try {
            com.google.gson.Gson g = new com.google.gson.Gson();
            String json = g.toJson(data);
            return g.fromJson(json, User.class);
        } catch (Exception ignored) { }
        return null;
    }

    public OrderStats getOrderStats() {
        try {
            com.google.gson.Gson g = new com.google.gson.Gson();
            String json = g.toJson(data);
            return g.fromJson(json, OrderStats.class);
        } catch (Exception ignored) { }
        return null;
    }

    public java.util.List<OrderOverview> getLatestOrders() {
        try {
            com.google.gson.Gson g = new com.google.gson.Gson();
            String json = g.toJson(data);
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.List<OrderOverview>>(){}.getType();
            return g.fromJson(json, type);
        } catch (Exception ignored) { }
        return new java.util.ArrayList<>();
    }

    public boolean isSuccess() {
        if (success != null) return success;
        if (error != null) return !error;
        return false;
    }

    public boolean isError() {
        if (error != null) return error;
        if (success != null) return !success;
        return true;
    }

    public String getMessage() {
        return message;
    }

    public Integer getAddressId() { return addressId; }

    public void setError(Boolean error) { this.error = error; }
    public void setSuccess(Boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
}
