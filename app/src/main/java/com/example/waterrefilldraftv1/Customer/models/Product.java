package com.example.waterrefilldraftv1.Customer.models;

public class Product {
    private int id;
    private String name;
    private String type;
    private int liters;
    private double price;
    private int imageResource; // Local drawable ID

    public Product(int id, String name, String type, int liters, double price, int imageResource) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.liters = liters;
        this.price = price;
        this.imageResource = imageResource;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getLiters() {
        return liters;
    }

    public double getPrice() {
        return price;
    }

    public int getImageResource() {
        return imageResource;
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

    public void setLiters(int liters) {
        this.liters = liters;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
