package com.example.waterrefilldraftv1.utils;

import com.example.waterrefilldraftv1.models.CartItem;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(CartItem cartItem) {
        // Check if product already exists in cart
        for (CartItem existingItem : cartItems) {
            if (existingItem.getProduct().getId() == cartItem.getProduct().getId()) {
                existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
                return;
            }
        }
        // Add new item if not exists
        cartItems.add(cartItem);
    }

    public void removeFromCart(CartItem cartItem) {
        cartItems.remove(cartItem);
    }

    public void updateQuantity(CartItem cartItem, int newQuantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == cartItem.getProduct().getId()) {
                item.setQuantity(newQuantity);
                break;
            }
        }
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public void clearCart() {
        cartItems.clear();
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public int getTotalItems() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getQuantity();
        }
        return total;
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
}