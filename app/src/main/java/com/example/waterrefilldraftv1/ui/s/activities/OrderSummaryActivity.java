package com.example.waterrefilldraftv1.ui.s.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.models.CartItem;
import com.example.waterrefilldraftv1.utils.CartManager;
import java.util.Calendar;
import java.util.List;

public class OrderSummaryActivity extends AppCompatActivity {

    private ImageView ivBack, ivGallonImage;
    private TextView tvCustomerName, tvContactNumber, tvSelectedAddress, tvSelectAddress;
    private TextView tvPickupTime, tvGallonType, tvTotalAmount, tvPaymentMethod;
    private Button btnPlaceOrder;
    private CartManager cartManager;

    private String selectedAddress = "St. Gabriella Bagino Barro...";
    private String selectedPickupTime = "";
    private String selectedPaymentMethod = "Cash on Delivery";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        cartManager = CartManager.getInstance();

        initViews();
        setupClickListeners();
        loadOrderData();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivGallonImage = findViewById(R.id.iv_gallon_image);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvContactNumber = findViewById(R.id.tv_contact_number);
        tvSelectedAddress = findViewById(R.id.tv_selected_address);
        tvSelectAddress = findViewById(R.id.tv_select_address);
        tvPickupTime = findViewById(R.id.tv_pickup_time);
        tvGallonType = findViewById(R.id.tv_gallon_type);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        tvSelectAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressSelectionActivity.class);
            startActivityForResult(intent, 100);
        });

        tvPickupTime.setOnClickListener(v -> showDateTimePicker());

        tvPaymentMethod.setOnClickListener(v -> showPaymentMethodDialog());

        btnPlaceOrder.setOnClickListener(v -> processOrder());
    }

    private void loadOrderData() {
        // Load customer data (would normally come from user session)
        tvCustomerName.setText("Chrisha Dalmacio");
        tvContactNumber.setText("09567894555");
        tvSelectedAddress.setText(selectedAddress);

        // Load cart items
        List<CartItem> cartItems = cartManager.getCartItems();
        if (!cartItems.isEmpty()) {
            CartItem firstItem = cartItems.get(0);
            ivGallonImage.setImageResource(firstItem.getProduct().getImageResource());

            StringBuilder gallonDetails = new StringBuilder();
            for (CartItem item : cartItems) {
                gallonDetails.append(item.getProduct().getType())
                        .append(" - ")
                        .append(item.getQuantity())
                        .append(": ")
                        .append(String.format("%.2f", item.getTotalPrice()))
                        .append("\n");
            }
            tvGallonType.setText(gallonDetails.toString().trim());
        }

        tvTotalAmount.setText(String.format("%.2f", cartManager.getTotalPrice()));
        tvPaymentMethod.setText(selectedPaymentMethod);
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // After date is selected, show time picker
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                selectedPickupTime = String.format("%02d/%02d/%d %02d:%02d",
                                        month + 1, dayOfMonth, year, hourOfDay, minute);
                                tvPickupTime.setText(selectedPickupTime);
                                tvPickupTime.setTextColor(getResources().getColor(android.R.color.black));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showPaymentMethodDialog() {
        String[] paymentMethods = {"Cash on Delivery", "PayPal"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Select Payment Method")
                .setItems(paymentMethods, (dialog, which) -> {
                    selectedPaymentMethod = paymentMethods[which];
                    tvPaymentMethod.setText(selectedPaymentMethod);
                });
        builder.show();
    }

    private void processOrder() {
        if (selectedPickupTime.isEmpty()) {
            tvPickupTime.setText("Please select pickup time");
            tvPickupTime.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            return;
        }

        if (selectedPaymentMethod.equals("PayPal")) {
            // Go to PayPal payment
            Intent intent = new Intent(this, PaymentMethodActivity.class);
            intent.putExtra("total_amount", cartManager.getTotalPrice());
            startActivity(intent);
        } else {
            // Cash on delivery - go directly to order confirmation
            showOrderConfirmation();
        }
    }

    private void showOrderConfirmation() {
        Intent intent = new Intent(this, OrderConfirmationActivity.class);
        startActivity(intent);

        // Clear cart after successful order
        cartManager.clearCart();

        // Close this activity
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedAddress = data.getStringExtra("selected_address");
            tvSelectedAddress.setText(selectedAddress);
        }
    }
}