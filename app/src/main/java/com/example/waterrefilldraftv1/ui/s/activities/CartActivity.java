package com.example.waterrefilldraftv1.ui.s.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.models.CartItem;
import com.example.waterrefilldraftv1.utils.CartManager;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private ImageView ivBack;
    private LinearLayout llCartItems;
    private Button btnCheckout;
    private CartManager cartManager;
    private boolean directCheckout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = CartManager.getInstance();
        directCheckout = getIntent().getBooleanExtra("direct_checkout", false);

        initViews();
        setupClickListeners();
        loadCartItems();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        llCartItems = findViewById(R.id.ll_cart_items);
        btnCheckout = findViewById(R.id.btn_checkout);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        btnCheckout.setOnClickListener(v -> {
            if (!cartManager.isEmpty()) {
                Intent intent = new Intent(CartActivity.this, OrderSummaryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadCartItems() {
        llCartItems.removeAllViews();
        List<CartItem> cartItems = cartManager.getCartItems();

        if (cartItems.isEmpty()) {
            showEmptyCartMessage();
            return;
        }

        for (CartItem cartItem : cartItems) {
            View itemView = createCartItemView(cartItem);
            llCartItems.addView(itemView);
        }

        updateCheckoutButton();

        // If direct checkout, proceed automatically
        if (directCheckout) {
            btnCheckout.performClick();
        }
    }

    private View createCartItemView(CartItem cartItem) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_cart, null);

        ImageView ivProductImage = itemView.findViewById(R.id.iv_product_image);
        TextView tvProductType = itemView.findViewById(R.id.tv_product_type);
        TextView tvProductLiters = itemView.findViewById(R.id.tv_product_liters);
        TextView tvProductPrice = itemView.findViewById(R.id.tv_product_price);
        TextView tvQuantity = itemView.findViewById(R.id.tv_quantity);
        TextView tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);

        // Set data
        ivProductImage.setImageResource(cartItem.getProduct().getImageResource());
        tvProductType.setText("Type: " + cartItem.getProduct().getType());
        tvProductLiters.setText("Liters: " + cartItem.getProduct().getLiters() + " liters");
        tvProductPrice.setText("Price: " + String.format("%.2f", cartItem.getProduct().getPrice()));
        tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
        tvTotalAmount.setText("Total Amount: " + String.format("%.2f", cartItem.getTotalPrice()));

        // Click to show order summary
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, OrderSummaryActivity.class);
            startActivity(intent);
        });

        return itemView;
    }

    private void showEmptyCartMessage() {
        TextView emptyMessage = new TextView(this);
        emptyMessage.setText("Your cart is empty");
        emptyMessage.setTextSize(16);
        emptyMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        emptyMessage.setPadding(0, 50, 0, 50);
        llCartItems.addView(emptyMessage);

        btnCheckout.setEnabled(false);
        btnCheckout.setText("Cart is Empty");
    }

    private void updateCheckoutButton() {
        double totalPrice = cartManager.getTotalPrice();
        int totalItems = cartManager.getTotalItems();

        btnCheckout.setEnabled(true);
        btnCheckout.setText("Proceed to Checkout (" + totalItems + " items - ₱" + String.format("%.2f", totalPrice) + ")");
    }
}