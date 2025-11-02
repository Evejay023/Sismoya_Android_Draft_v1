package com.example.waterrefilldraftv1.Riders.Utils;

public class StatusFormatter {


    public static String format(String status) {
        if (status == null) return "Unknown";

        switch (status.toLowerCase()) {
            case "to_pickup":
                return "To Pick Up";
            case "to_deliver":
                return "To Deliver";
            case "picked_up":
                return "Picked Up";
            case "delivered":
                return "Delivered";
            case "cancelled":
                return "Cancelled";
            case "pending":
                return "Pending";
            default:
                // Capitalize first letter fallback
                return status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
        }
    }

}
