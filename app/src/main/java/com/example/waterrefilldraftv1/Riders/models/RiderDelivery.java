package com.example.waterrefilldraftv1.Riders.models;

import com.google.gson.annotations.SerializedName;

public class RiderDelivery {
    @SerializedName("order_id") private int orderId;
    @SerializedName("customer_name") private String customerName;
    @SerializedName("address") private String address;
    @SerializedName("contact_number") private String contactNumber;
    @SerializedName("status") private String status;
    @SerializedName("payment_method") private String paymentMethod;
    @SerializedName("total_amount") private double totalAmount;

    public RiderDelivery(int orderId, String customerName, String address,
                         String contactNumber, String status,
                         String paymentMethod, double totalAmount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
    }

    public int getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getAddress() { return address; }
    public String getContactNumber() { return contactNumber; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotalAmount() { return totalAmount; }
}
