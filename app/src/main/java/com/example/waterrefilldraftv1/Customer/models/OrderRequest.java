package com.example.waterrefilldraftv1.Customer.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderRequest {
    @SerializedName("items")
    private List<OrderRequestItem> items;

    @SerializedName("address_id")
    private Integer addressId; // optional; backend uses default if null

    @SerializedName("pickup_datetime")
    private String pickupDatetime; // YYYY-MM-DD HH:mm:ss

    @SerializedName("payment_method")
    private String paymentMethod; // "COD" or "PAYPAL"

    public OrderRequest(List<OrderRequestItem> items, Integer addressId, String pickupDatetime, String paymentMethod) {
        this.items = items;
        this.addressId = addressId;
        this.pickupDatetime = pickupDatetime;
        this.paymentMethod = paymentMethod;
    }

    public static class OrderRequestItem {
        @SerializedName("gallon_id")
        public int gallonId;
        @SerializedName("quantity")
        public int quantity;

        public OrderRequestItem(int gallonId, int quantity) {
            this.gallonId = gallonId;
            this.quantity = quantity;
        }
    }
}




