package com.example.waterrefilldraftv1.Customer.UserInterface.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Customer.utils.CartManager;

public class PaymentMethodActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTotalAmount;
    private Button btnPay;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        totalAmount = getIntent().getDoubleExtra("total_amount", 0.0);

        initViews();
        setupClickListeners();
        loadPaymentData();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        btnPay = findViewById(R.id.btn_pay);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        btnPay.setOnClickListener(v -> processPayPalPayment());
    }

    private void loadPaymentData() {
        tvTotalAmount.setText(String.format("%.0f", totalAmount));
    }

    private void processPayPalPayment() {
        // In a real app, this would integrate with PayPal SDK
        // For demo purposes, we'll simulate the payment process

        // Simulate PayPal validation and payment
        simulatePayPalValidation();
    }

    private void simulatePayPalValidation() {
        // Show loading or progress indicator here in real implementation

        // Simulate successful payment after a delay
        btnPay.setEnabled(false);
        btnPay.setText("Processing...");

        // Simulate network delay
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 2 second delay

                runOnUiThread(() -> {
                    // Payment successful, proceed to order confirmation
                    Intent intent = new Intent(PaymentMethodActivity.this, OrderConfirmationActivity.class);
                    startActivity(intent);

                    // Clear cart
                    CartManager.getInstance().clearCart();

                    // Finish this activity and return to main
                    finish();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}