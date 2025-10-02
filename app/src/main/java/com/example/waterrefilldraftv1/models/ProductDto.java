package com.example.waterrefilldraftv1.models;

import com.google.gson.annotations.SerializedName;

public class ProductDto {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;  // e.g. Round Gallon, Slim Gallon

    @SerializedName("capacity")
    private String capacity;  // liters (could be string or int depending on API)

    @SerializedName("price")
    private double price;

    @SerializedName("status")
    private String status; // e.g. Available, Out of Stock

    @SerializedName("image_url")
    private String imageUrl; // optional: if backend returns product image

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getCapacity() { return capacity; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setCapacity(String capacity) { this.capacity = capacity; }
    public void setPrice(double price) { this.price = price; }
    public void setStatus(String status) { this.status = status; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
