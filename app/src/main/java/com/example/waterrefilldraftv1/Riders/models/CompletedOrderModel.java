package com.example.waterrefilldraftv1.Riders.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class CompletedOrderModel implements Serializable {

    @SerializedName("order_id")
    private String orderId; // CHANGED: String instead of int

    @SerializedName("delivered_at")
    private String deliveredAt;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("contact_no")
    private String contactNo;

    @SerializedName("address")
    private String address;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("payment_status")
    private String paymentStatus;

    @SerializedName("total_price")
    private String totalPrice;

    @SerializedName("status")
    private String status;

    @SerializedName("items")
    private List<OrderItem> items;

    // Getters
    public String getOrderId() { return orderId; } // CHANGED: String return type
    public String getDeliveredAt() {
        return deliveredAt != null ? deliveredAt : "";
    }
    public String getCustomerName() { return customerName; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getContactNo() { return contactNo; }
    public String getAddress() { return address != null ? address : "No Address Provided"; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public List<OrderItem> getItems() { return items; }

    // Helper methods for image and gallon name
    public String getPrimaryGallonName() {
        if (items != null && !items.isEmpty()) {
            return items.get(0).getGallonName() != null ? items.get(0).getGallonName() : "Gallon";
        }
        return "Gallon";
    }

    public String getPrimaryImageUrl() {
        if (items != null && !items.isEmpty()) {
            String imagePath = items.get(0).getImageUrl();
            if (imagePath == null || imagePath.trim().isEmpty()) return null;

            String path = imagePath.trim();
            if (path.startsWith("http://") || path.startsWith("https://")) {
                return path;
            }
            return "https://sismoya.bsit3b.site/" + path.replaceFirst("^/+", "");
        }
        return null;
    }

    public int getPrimaryQuantity() {
        if (items != null && !items.isEmpty()) {
            return items.get(0).getQuantity();
        }
        return 0;
    }

    // Format total price with currency
    public String getFormattedTotal() {
        try {
            double total = Double.parseDouble(totalPrice);
            return String.format("₱%.2f", total);
        } catch (NumberFormatException e) {
            return "₱0.00";
        }
    }
}