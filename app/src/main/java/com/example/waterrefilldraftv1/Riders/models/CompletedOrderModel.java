package com.example.waterrefilldraftv1.Riders.models;

import java.io.Serializable;
import java.util.List;

public class CompletedOrderModel implements Serializable {
    private int order_id;
    private String delivered_at;
    private String customer_name;
    private String first_name;
    private String last_name;
    private String contact_no;
    private String address;
    private String payment_method;
    private String payment_status;
    private String total_price;
    private String status;

    private List<OrderItem> items;

    public int getOrder_id() { return order_id; }
    public String getDelivered_at() { return delivered_at; }
    public String getCustomer_name() { return customer_name; }
    public String getFirst_name() { return first_name; }
    public String getLast_name() { return last_name; }
    public String getContact_no() { return contact_no; }
    public String getAddress() { return address; }
    public String getPayment_method() { return payment_method; }
    public String getPayment_status() { return payment_status; }
    public String getTotal_price() { return total_price; }
    public String getStatus() { return status; }
    public List<OrderItem> getItems() { return items; }
}
