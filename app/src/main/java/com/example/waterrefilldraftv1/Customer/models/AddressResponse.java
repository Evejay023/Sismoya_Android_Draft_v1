package com.example.waterrefilldraftv1.Customer.models;

import java.util.List;

public class AddressResponse {
    private boolean success;
    private List<Address> addresses;
    private int count;

    public boolean isSuccess() {
        return success;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public int getCount() {
        return count;
    }
}
