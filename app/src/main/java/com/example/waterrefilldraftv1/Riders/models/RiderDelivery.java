package com.example.waterrefilldraftv1.Riders.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RiderDelivery {
    @SerializedName("order_id") private String orderId;
    @SerializedName("customer_name") private String customerName;
    @SerializedName("address") private String address;
    @SerializedName("contact_number") private String contactNumber;
    @SerializedName("status") private String status;
    @SerializedName("payment_method") private String paymentMethod;
    @SerializedName("total_amount") private double totalAmount;
    @SerializedName("items") private List<PickupOrder.Item> items;

    private static final String BASE_IMAGE_HOST = "https://sismoya.bsit3b.site/";

    public RiderDelivery(String orderId, String customerName, String address,
                         String contactNumber, String status,
                         String paymentMethod, double totalAmount,
                         List<PickupOrder.Item> items) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getAddress() { return address; }
    public String getContactNumber() { return contactNumber; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotalAmount() { return totalAmount; }
    public List<PickupOrder.Item> getItems() { return items; }

    // Helper methods for primary item
    public String getGallonName() {
        if (items != null && !items.isEmpty()) {
            return items.get(0).gallonName != null ? items.get(0).gallonName : "Water Gallon";
        }
        return "Water Gallon";
    }

    public String getImageUrl() {
        if (items != null && !items.isEmpty()) {
            return items.get(0).imageUrl;
        }
        return null;
    }

    // ✅ FIXED: Add proper image URL formatting
    public String getFullImageUrl() {
        String imageUrl = getImageUrl();
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }

        String path = imageUrl.trim();
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }

        return BASE_IMAGE_HOST + path.replaceFirst("^/+", "");
    }

    public int getGallonQuantity() {
        if (items != null && !items.isEmpty()) {
            return items.get(0).quantity;
        }
        return 0;
    }

    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public boolean hasMultipleItems() {
        return items != null && items.size() > 1;
    }

    // ✅ ADD: Compatibility methods to match PickupOrder pattern
    public String getPrimaryImageUrl() {
        return getFullImageUrl();
    }

    public int getPrimaryQuantity() {
        return getGallonQuantity();
    }

    public String getPrimaryGallonName() {
        return getGallonName();
    }
}