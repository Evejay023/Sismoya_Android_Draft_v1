package com.example.waterrefilldraftv1.Global.network;

import com.example.waterrefilldraftv1.Customer.models.OrderOverview;
import com.example.waterrefilldraftv1.Customer.models.OrderStats;
import com.example.waterrefilldraftv1.Customer.models.User;
import com.example.waterrefilldraftv1.Riders.models.Rider;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;


public class ApiResponse {

    @SerializedName("error")
    private Boolean error;

    @SerializedName("success")
    private Boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("address_id")
    private Integer addressId;

    // Backend may return either "user" or "rider" depending on role
    @SerializedName("user")
    private Object user;

    @SerializedName("rider")
    private Rider rider;

    @SerializedName("data")
    private Object data;

    // ✅ Return User (customer) safely
    public User getUser() {
        try {
            Gson g = new Gson();

            if (user != null) {
                String json = g.toJson(user);
                return g.fromJson(json, User.class);
            }

            if (data != null) {
                String json = g.toJson(data);
                return g.fromJson(json, User.class);
            }
        } catch (Exception ignored) {}

        return null;
    }

    // ✅ Return Rider safely even if backend used "user" key
    public Rider getRider() {
        try {
            Gson g = new Gson();

            if (rider != null) return rider;

            if (user != null) {
                String json = g.toJson(user);
                return g.fromJson(json, Rider.class);
            }

            if (data != null) {
                String json = g.toJson(data);
                return g.fromJson(json, Rider.class);
            }
        } catch (Exception ignored) {}

        return null;
    }

    public OrderStats getOrderStats() {
        try {
            Gson g = new Gson();
            String json = g.toJson(data);
            return g.fromJson(json, OrderStats.class);
        } catch (Exception ignored) {}
        return null;
    }

    public List<OrderOverview> getLatestOrders() {
        try {
            Gson g = new Gson();
            String json = g.toJson(data);
            Type type = new TypeToken<List<OrderOverview>>(){}.getType();
            return g.fromJson(json, type);
        } catch (Exception ignored) {}
        return new ArrayList<>();
    }

    // ✅ Simplified success/error logic
    public boolean isSuccess() {
        if (success != null) return success;
        if (error != null) return !error;
        return false;
    }

    public <T> List<T> getDataAsList(Class<T> clazz) {
        try {
            Gson gson = new Gson();
            Type type = TypeToken.getParameterized(List.class, clazz).getType();
            return gson.fromJson((JsonElement) data, type);
        } catch (Exception e) {
            return null;
        }
    }


    public boolean isError() {
        return !isSuccess();
    }

    public String getMessage() {
        return message != null ? message : "";
    }

    public Integer getAddressId() { return addressId; }

    public void setError(Boolean error) { this.error = error; }
    public void setSuccess(Boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
}
