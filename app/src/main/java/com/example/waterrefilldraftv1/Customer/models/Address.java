package com.example.waterrefilldraftv1.Customer.models;

import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("address_id")
    private int addressId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("label")
    private String label;

    @SerializedName("address")
    private String address;

    // ✅ FIX: use int instead of boolean because backend sends 0/1
    @SerializedName("is_default")
    private int isDefault;

    public int getAddressId() { return addressId; }
    public int getUserId() { return userId; }
    public String getLabel() { return label; }
    public String getAddress() { return address; }

    // ✅ Helper method to easily check boolean
    public boolean isDefault() {
        return isDefault == 1;
    }

    public void setLabel(String label) { this.label = label; }
    public void setAddress(String address) { this.address = address; }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault ? 1 : 0;
    }
}
