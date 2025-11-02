package com.example.waterrefilldraftv1.Riders.models;

import com.google.gson.annotations.SerializedName;

public class PickupOrder {

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("address_id")
    private int addressId;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("address")
    private String address;

    @SerializedName("contact_no")
    private String contactNumber;


    @SerializedName("status")
    private String status;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("payment_status")
    private String paymentStatus;

    @SerializedName("total_price")
    private double totalAmount;

    @SerializedName("pickup_datetime")
    private String pickupDatetime;

    // --- Getters ---
    public int getOrderId() { return orderId; }
    public int getUserId() { return userId; }
    public int getAddressId() { return addressId; }
    public String getCustomerName() { return customerName; }
    public String getAddress() { return address; }
    public String getContactNumber() { return contactNumber; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public double getTotalAmount() { return totalAmount; }
    public String getPickupDatetime() { return pickupDatetime; }
}
