package com.example.waterrefilldraftv1.ui.s.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.models.CartItem;
import com.example.waterrefilldraftv1.ui.s.dialog.CustomDateTimePickerDialog;
import com.example.waterrefilldraftv1.utils.CartManager;
import java.util.List;

public class OrderSummaryActivity extends AppCompatActivity {

    private ImageView ivBack, ivGallonImage;
    private TextView tvCustomerName, tvContactNumber, tvSelectedAddress, tvSelectAddress;
    private TextView tvPickupTime, tvGallonType, tvTotalAmount, tvPaymentMethod;
    private Button btnPlaceOrder;
    private CartManager cartManager;

    private String selectedAddress = "St. Gabriella Bagino Barro...";
    private String selectedPickupTime = ""; // For backend (YYYY-MM-DD HH:mm:ss)
    private String selectedPickupDisplay = ""; // For display
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

        // ✅ Use custom DateTime picker dialog
        tvPickupTime.setOnClickListener(v -> showCustomDateTimePicker());

        tvPaymentMethod.setOnClickListener(v -> showPaymentMethodDialog());

        btnPlaceOrder.setOnClickListener(v -> processOrder());
    }

    private void showCustomDateTimePicker() {
        CustomDateTimePickerDialog dialog = CustomDateTimePickerDialog.newInstance(
                new CustomDateTimePickerDialog.OnDateTimeSelectedListener() {
                    @Override
                    public void onDateTimeSelected(String backendFormat, String displayFormat) {
                        selectedPickupTime = backendFormat; // For backend
                        selectedPickupDisplay = displayFormat; // For display

                        tvPickupTime.setText(displayFormat);
                        tvPickupTime.setTextColor(getResources().getColor(android.R.color.black));
                    }
                }
        );

        dialog.show(getSupportFragmentManager(), "custom_datetime_picker");
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
                        .append(": ₱")
                        .append(String.format("%.2f", item.getTotalPrice()))
                        .append("\n");
            }
            tvGallonType.setText(gallonDetails.toString().trim());
        }

        tvTotalAmount.setText("₱" + String.format("%.2f", cartManager.getTotalPrice()));
        tvPaymentMethod.setText(selectedPaymentMethod);

        // Set placeholder text for pickup time
        tvPickupTime.setText("Select pickup date & time");
        tvPickupTime.setTextColor(getResources().getColor(android.R.color.darker_gray));
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
        // Check if pickup time is selected
        if (selectedPickupTime.isEmpty()) {
            tvPickupTime.setText("Please select pickup time");
            tvPickupTime.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

            android.widget.Toast.makeText(this, "Please select a pickup date and time", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedPaymentMethod.equals("PayPal")) {
            // Go to PayPal payment
            Intent intent = new Intent(this, PaymentMethodActivity.class);
            intent.putExtra("total_amount", cartManager.getTotalPrice());
            intent.putExtra("pickup_datetime", selectedPickupTime);
            startActivity(intent);
        } else {
            // Cash on delivery - go directly to order confirmation
            showOrderConfirmation();
        }
    }

    private void showOrderConfirmation() {
        Intent intent = new Intent(this, OrderConfirmationActivity.class);
        intent.putExtra("pickup_time", selectedPickupDisplay);
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