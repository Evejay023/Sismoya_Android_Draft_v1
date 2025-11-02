package com.example.waterrefilldraftv1.Customer.utils;

import com.example.waterrefilldraftv1.Customer.models.CartItem;
import com.example.waterrefilldraftv1.Customer.models.Product;
import com.example.waterrefilldraftv1.Customer.models.ServerCartItem;
import com.example.waterrefilldraftv1.Global.network.ApiService;
import com.example.waterrefilldraftv1.Global.network.RetrofitClient;
import android.content.Context;
import android.content.SharedPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private final List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // ===== Backend sync helpers =====
    private String getToken(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sp.getString("token", null);
    }

    public void syncFromServer(Context ctx, Callback<java.util.List<ServerCartItem>> cb) {
        String token = getToken(ctx);
        if (token == null) { cb.onFailure(null, new Throwable("No token")); return; }
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getCartItems("Bearer " + token).enqueue(new Callback<java.util.List<ServerCartItem>>() {
            @Override public void onResponse(Call<java.util.List<ServerCartItem>> call, Response<java.util.List<ServerCartItem>> res) {
                cb.onResponse(call, res);
            }
            @Override public void onFailure(Call<java.util.List<ServerCartItem>> call, Throwable t) { cb.onFailure(call, t); }
        });
    }

    // ✅ Add product to cart
    public void addToCart(CartItem cartItem) {
        for (CartItem existingItem : cartItems) {
            if (existingItem.getProduct().getId() == cartItem.getProduct().getId()) {
                existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
                return;
            }
        }
        cartItems.add(cartItem);
    }

    // ✅ Remove a specific cart item (used in CartActivity)
    public void removeItem(Product product) {
        CartItem toRemove = null;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                toRemove = item;
                break;
            }
        }
        if (toRemove != null) {
            cartItems.remove(toRemove);
        }
    }

    // ✅ Update quantity safely
    public void updateQuantity(Product product, int newQuantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(newQuantity);
                break;
            }
        }
    }

    // ✅ Get all cart items
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    // ✅ Get selected items; if none selected, return all (for checkout)
    public List<CartItem> getSelectedOrAllItems() {
        List<CartItem> selected = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                selected.add(item);
            }
        }
        if (!selected.isEmpty()) return selected;
        return getCartItems();
    }

    // ✅ Clear the entire cart
    public void clearCart() {
        cartItems.clear();
    }

    // ✅ Get total price of all selected items only
    public double getSelectedTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                total += item.getTotalPrice();
            }
        }
        return total;
    }

    // ✅ Get total quantity of selected items only
    public int getSelectedTotalItems() {
        int total = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                total += item.getQuantity();
            }
        }
        return total;
    }

    // ✅ Get total of all (if you ever need it)
    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    // ✅ Check if cart is empty
    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
}