package com.example.waterrefilldraftv1.models;


public class WaterContainer {
    private String name;
    private String liters;
    private String price;
    private int imageResourceId;
    private boolean available;

    public WaterContainer(String name, String liters, String price, int imageResourceId, boolean available) {
        this.name = name;
        this.liters = liters;
        this.price = price;
        this.imageResourceId = imageResourceId;
        this.available = available;
    }

    public String getName() {
        return name;
    }

    public String getLiters() {
        return liters;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
