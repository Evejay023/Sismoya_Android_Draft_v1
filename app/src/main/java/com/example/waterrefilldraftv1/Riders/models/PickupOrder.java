package com.example.waterrefilldraftv1.Riders.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PickupOrder implements Serializable {

    // NOTE: if you change base domain, update BASE_IMAGE_HOST
    private static final String BASE_IMAGE_HOST = "https://sismoya.bsit3b.site/";

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("pickup_datetime")
    private String pickupDatetime;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("payment_status")
    private String paymentStatus;

    @SerializedName("total_price")
    private String totalPrice;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("status")
    private String status;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("contact_number")
    private String contactNumber;

    @SerializedName("address")
    private String address;

    @SerializedName("items")
    private List<Item> items;

    // OPTIONALS SEEN IN YOUR PAYLOAD
    @SerializedName("payment_proof")
    private String paymentProof;

    @SerializedName("notes")
    private String notes;

    @SerializedName("user_id")
    private Integer userId;

    @SerializedName("address_id")
    private Integer addressId;

    // ------------------- Basic getters -------------------
    public int getOrderId() { return orderId; }
    public String getPickupDatetime() { return pickupDatetime; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getTotalPrice() { return totalPrice; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getStatus() { return status; }
    public String getCustomerName() { return customerName; }
    public String getContactNumber() { return contactNumber; }
    public String getAddress() { return address != null ? address : "No Address Provided"; }
    public List<Item> getItems() { return items; }
    public Integer getUserId() { return userId; }
    public Integer getAddressId() { return addressId; }
    public String getPaymentProof() { return paymentProof; }
    public String getNotes() { return notes; }

    // ------------------- Numeric helpers -------------------
    public double getTotalPriceDouble() {
        if (totalPrice == null) return 0.0;
        try {
            return Double.parseDouble(totalPrice);
        } catch (NumberFormatException e) {
            String cleaned = totalPrice.replaceAll("[^0-9.\\-]", "");
            try { return Double.parseDouble(cleaned); } catch (Exception ex) { return 0.0; }
        }
    }

    // ------------------- Primary item convenience -------------------
    public String getPrimaryGallonName() {
        if (items != null && !items.isEmpty()) {
            String n = items.get(0).gallonName;
            return n != null ? n : "Gallon";
        }
        return "Gallon";
    }

    public String getPrimaryImagePath() {
        if (items != null && !items.isEmpty()) {
            return items.get(0).imageUrl;
        }
        return null;
    }

    public String getPrimaryImageUrl() {
        String p = getPrimaryImagePath();
        if (p == null || p.trim().isEmpty()) return null;
        p = p.trim();

        if (p.startsWith("http://") || p.startsWith("https://")) {
            return p;
        }

        return BASE_IMAGE_HOST + p.replaceFirst("^/+", "");
    }

    public int getPrimaryQuantity() {
        if (items != null && !items.isEmpty()) {
            return items.get(0).quantity;
        }
        return 0;
    }

    // ------------------- Extra helpers -------------------
    /** Useful for a UI badge: "x items" */
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public boolean hasMultipleGallons() {
        return items != null && items.size() > 1;
    }

    // ------------------- Datetime formatting -------------------
    /** Returns formatted pickup datetime -> "Nov 03, 3:45 PM" */
    public String getFormattedPickupDatetime() {
        if (pickupDatetime == null) return "";

        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .parse(pickupDatetime);

            if (d != null) {
                return new SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
                        .format(d);
            }
        } catch (ParseException ignored) {}

        return pickupDatetime;
    }

    // ------------------- Status helpers -------------------
    public String getDisplayStatus() {
        if (status == null) return "";
        switch (status.toLowerCase()) {
            case "to_pickup": return "To Pick-Up";
            case "to_deliver": return "To Deliver";
            case "picked_up": return "Picked Up";
            case "delivered": return "Delivered";
            case "cancelled": return "Cancelled";
            default:
                String s = status.toLowerCase();
                return s.substring(0,1).toUpperCase() + (s.length() > 1 ? s.substring(1) : "");
        }
    }

    public boolean isToPickup() { return "to_pickup".equalsIgnoreCase(status); }
    public boolean isToDeliver() { return "to_deliver".equalsIgnoreCase(status); }
    public boolean isPickedUp() { return "picked_up".equalsIgnoreCase(status); }
    public boolean isDelivered() { return "delivered".equalsIgnoreCase(status); }
    public boolean isCancelled() { return "cancelled".equalsIgnoreCase(status); }

    // ------------------- Currency formatting -------------------
    public String getFormattedTotal() {
        return String.format(Locale.getDefault(), "₱%.2f", getTotalPriceDouble());
    }

    public String getSummary() {
        return (customerName != null ? customerName : "Customer") + " • " + getFormattedTotal();
    }

    public double getTotalAmount() {
        return getTotalPriceDouble();
    }


    // ------------------- Inner Item class -------------------
    public static class Item implements Serializable {
        @SerializedName("gallon_id")
        public int gallonId;

        @SerializedName("gallon_name")
        public String gallonName;

        @SerializedName("image_url")
        public String imageUrl;

        @SerializedName("quantity")
        public int quantity;

        public String getFullImageUrl() {
            if (imageUrl == null) return null;
            String p = imageUrl.trim();
            if (p.startsWith("http://") || p.startsWith("https://")) return p;
            return BASE_IMAGE_HOST + p.replaceFirst("^/+", "");
        }

        public String getDisplayQuantity() {
            return "Qty: " + quantity;
        }


    }


}
