package com.example.waterrefilldraftv1.Customer.models;

public class CartItem {
    private Product product;
    private int quantity;
    private boolean selected; // for checkbox selection

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.selected = false; // default state
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    public void increaseQuantity() {
        quantity++;
    }

    public void decreaseQuantity() {
        if (quantity > 1) quantity--;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}