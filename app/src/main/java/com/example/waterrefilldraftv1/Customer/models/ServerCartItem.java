package com.example.waterrefilldraftv1.Customer.models;

import com.google.gson.annotations.SerializedName;

public class ServerCartItem {
    @SerializedName("cart_item_id")
    public int cartItemId;

    @SerializedName("gallon_id")
    public int gallonId;

    @SerializedName("quantity")
    public int quantity;

    @SerializedName("name")
    public String name;

    @SerializedName("size")
    public String size;

    @SerializedName("price")
    public double price;

    @SerializedName("image_url")
    public String imageUrl;

    @SerializedName("total_price")
    public double totalPrice;

    // UI-only state
    private boolean selected;

    public boolean isSelected() { return selected; }
    public void setSelected(boolean s) { selected = s; }
}









