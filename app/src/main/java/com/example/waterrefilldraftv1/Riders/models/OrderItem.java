package com.example.waterrefilldraftv1.Riders.models;

import com.google.gson.annotations.SerializedName;

public class OrderItem {
    @SerializedName("order_item_id")
    private int orderItemId;

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("gallon_id")
    private int gallonId;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("price")
    private String price;

    @SerializedName("total_price")
    private String totalPrice;

    @SerializedName("gallon_name")
    private String gallonName;

    @SerializedName("image_url")
    private String imageUrl;

    // Getters
    public int getOrderItemId() { return orderItemId; }
    public int getOrderId() { return orderId; }
    public int getGallonId() { return gallonId; }
    public int getQuantity() { return quantity; }
    public String getPrice() { return price; }
    public String getTotalPrice() { return totalPrice; }
    public String getGallonName() { return gallonName; }
    public String getImageUrl() { return imageUrl; }
}