package com.example.waterrefilldraftv1.Riders.models;

import com.google.gson.annotations.SerializedName;

public class RiderDelivery {
    @SerializedName("order_id") private String orderId; // CHANGED: String instead of int
    @SerializedName("customer_name") private String customerName;
    @SerializedName("address") private String address;
    @SerializedName("contact_number") private String contactNumber;
    @SerializedName("status") private String status;
    @SerializedName("payment_method") private String paymentMethod;
    @SerializedName("total_amount") private double totalAmount;

    // Add these new fields
    @SerializedName("gallon_name") private String gallonName;
    @SerializedName("gallon_quantity") private int gallonQuantity;
    @SerializedName("image_url") private String imageUrl;
    @SerializedName("item_count") private int itemCount;

    public RiderDelivery(String orderId, String customerName, String address, // CHANGED: String orderId
                         String contactNumber, String status,
                         String paymentMethod, double totalAmount,
                         String gallonName, int gallonQuantity,
                         String imageUrl, int itemCount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.gallonName = gallonName;
        this.gallonQuantity = gallonQuantity;
        this.imageUrl = imageUrl;
        this.itemCount = itemCount;
    }

    public String getOrderId() { return orderId; } // CHANGED: String return type
    public String getCustomerName() { return customerName; }
    public String getAddress() { return address; }
    public String getContactNumber() { return contactNumber; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotalAmount() { return totalAmount; }

    public String getGallonName() { return gallonName != null ? gallonName : "Water Gallon"; }
    public int getGallonQuantity() { return gallonQuantity; }
    public String getImageUrl() { return imageUrl; }
    public int getItemCount() { return itemCount; }
    public boolean hasMultipleItems() { return itemCount > 1; }
}