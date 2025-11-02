package com.example.waterrefilldraftv1.Riders.models;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private int order_item_id;
    private int gallon_id;
    private int quantity;
    private String price;
    private String total_price;
    private String gallon_name;
    private String image_url;

    public int getOrder_item_id() { return order_item_id; }
    public int getGallon_id() { return gallon_id; }
    public int getQuantity() { return quantity; }
    public String getPrice() { return price; }
    public String getTotal_price() { return total_price; }
    public String getGallon_name() { return gallon_name; }
    public String getImage_url() { return image_url; }
}
