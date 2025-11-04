package com.example.waterrefilldraftv1.Customer.models;

import com.google.gson.annotations.SerializedName;

public class OrderStats {

    @SerializedName("pending")
    private int pending;

    @SerializedName("completed")
    private int completed;

    @SerializedName("cancelled")
    private int cancelled;

    @SerializedName("total")
    private int total;

    // ✅ Add Getters
    public int getPending() {
        return pending;
    }

    public int getCompleted() {
        return completed;
    }

    public int getCancelled() {
        return cancelled;
    }

    public int getTotal() {
        return total;
    }
}
