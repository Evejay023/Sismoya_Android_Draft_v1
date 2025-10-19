package com.example.waterrefilldraftv1.Customer.models;

public class WaterContainer {
    private String name;
    private String price;
    private int imageResourceId;

    private int liters; // ✅ renamed field


    public WaterContainer(String name, String price, int imageResourceId) {
        this.name = name;
        this.price = price;
        this.imageResourceId = imageResourceId;
        this.liters = liters;

    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public int getLiters() { return liters; }
}
