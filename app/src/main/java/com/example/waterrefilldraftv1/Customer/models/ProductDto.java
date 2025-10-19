package com.example.waterrefilldraftv1.Customer.models;

import com.google.gson.annotations.SerializedName;

public class ProductDto {

    @SerializedName("id")
    private int id;

    // Some endpoints return gallon_id instead of id
    @SerializedName("gallon_id")
    private Integer gallonId;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;  // e.g. Round Gallon, Slim Gallon

    // Backend may send either capacity or size (e.g., "5L")
    @SerializedName("capacity")
    private String capacity;  // liters (string or int)

    @SerializedName("size")
    private String size;      // alternative field name used by backend

    @SerializedName("price")
    private double price;

    @SerializedName("status")
    private String status; // e.g. Available, Out of Stock

    @SerializedName("image_url")
    private String imageUrl; // URL from backend (optional)

    // Getters
    public int getId() {
        if (id != 0) return id;
        if (gallonId != null) return gallonId;
        return 0;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCapacity() {
        return capacity != null ? capacity : size;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
