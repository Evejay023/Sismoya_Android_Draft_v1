package com.example.waterrefilldraftv1.Riders.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.waterrefilldraftv1.Riders.models.Rider;
import com.google.gson.Gson;

public class RiderSessionStore {

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public RiderSessionStore(Context context) {
        prefs = context.getSharedPreferences("rider_session", Context.MODE_PRIVATE);
    }

    public void saveRider(Rider rider) {
        prefs.edit().putString("rider", gson.toJson(rider)).apply();
    }

    public Rider getRider() {
        String j = prefs.getString("rider", null);
        return j == null ? null : gson.fromJson(j, Rider.class);
    }

    // ✅ Add method to check if rider is logged in
    public boolean isRiderLoggedIn() {
        Rider rider = getRider();
        return rider != null && "rider".equalsIgnoreCase(rider.getRole());
    }

    public void savePickupOrders(String json) {
        prefs.edit().putString("pickup_orders", json).apply();
    }

    public void saveDeliveryOrders(String json) {
        prefs.edit().putString("delivery_orders", json).apply();
    }

    public String getPickupOrders() {
        return prefs.getString("pickup_orders", "");
    }

    public String getDeliveryOrders() {
        return prefs.getString("delivery_orders", "");
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}