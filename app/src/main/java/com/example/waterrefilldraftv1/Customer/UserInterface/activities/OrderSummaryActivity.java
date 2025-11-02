package com.example.waterrefilldraftv1.Customer.UserInterface.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Customer.models.CartItem;
import com.example.waterrefilldraftv1.Customer.models.OrderRequest;
import com.example.waterrefilldraftv1.Global.network.ApiResponse;
import com.example.waterrefilldraftv1.Global.network.NetworkManager;
import com.example.waterrefilldraftv1.Customer.UserInterface.dialog.CustomDateTimePickerDialog;
import com.example.waterrefilldraftv1.Customer.utils.CartManager;
import java.util.List;

public class OrderSummaryActivity extends AppCompatActivity {

    private ImageView ivBack, ivGallonImage;
    private TextView tvCustomerName, tvContactNumber, tvSelectedAddress;
    private TextView tvGallonInfo, tvItemTotal, tvTotalAmount;
    private Button btnSelectTime, btnPaymentMethod;
    private Button btnPlaceOrder;
    private CartManager cartManager;

    private String selectedAddress = "";
    private Integer selectedAddressId = null;
    private String selectedPickupTime = ""; // For backend (YYYY-MM-DD HH:mm:ss)
    private String selectedPickupDisplay = ""; // For display
    private String selectedPaymentMethod = "Cash on Delivery";
    private NetworkManager networkManager;
    private boolean isBuyNow = false;
    private int buyNowGallonId = 0;
    private int buyNowQuantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_dialog_order_summary);

        cartManager = CartManager.getInstance();
        networkManager = new NetworkManager(this);

        initViews();
        setupClickListeners();
        loadOrderData();

        // If opened from Buy Now, capture payload + prefill UI
        Intent i = getIntent();
        if (i != null && i.getBooleanExtra("buy_now", false)) {
            isBuyNow = true;
            String name = i.getStringExtra("name");
            String liters = i.getStringExtra("liters");
            double price = i.getDoubleExtra("price", 0);
            int imageRes = i.getIntExtra("image_res", 0);
            int quantity = i.getIntExtra("quantity", 1);
            buyNowGallonId = i.getIntExtra("gallon_id", 0);
            buyNowQuantity = quantity;
            if (imageRes != 0) ivGallonImage.setImageResource(imageRes);
            StringBuilder details = new StringBuilder();
            details.append(name!=null?name:"Gallon")
                    .append(" x")
                    .append(quantity)
                    .append(" - ")
                    .append(String.format("%.2f", price*quantity));
            tvGallonInfo.setText(details.toString());
            tvItemTotal.setText(String.format("%.2f", price*quantity));
            tvTotalAmount.setText("₱" + String.format("%.2f", price*quantity));
        }
    }

    private void initViews() {
        ivBack = findViewById(R.id.btn_back);
        ivGallonImage = findViewById(R.id.iv_container);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvContactNumber = findViewById(R.id.tv_contact_no);
        tvSelectedAddress = findViewById(R.id.tv_address);
        btnSelectTime = findViewById(R.id.btn_select_time);
        tvGallonInfo = findViewById(R.id.tv_container_info);
        tvItemTotal = findViewById(R.id.tv_item_total);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        btnPaymentMethod = findViewById(R.id.btn_payment_method);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        findViewById(R.id.btn_edit_address).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressSelectionActivity.class);
            startActivityForResult(intent, 100);
        });
        // Also allow tapping the address text to edit
        tvSelectedAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressSelectionActivity.class);
            startActivityForResult(intent, 100);
        });

        // ✅ Use custom DateTime picker dialog
        btnSelectTime.setOnClickListener(v -> showCustomDateTimePicker());

        btnPaymentMethod.setOnClickListener(v -> showPaymentMethodDialog());

        btnPlaceOrder.setOnClickListener(v -> processOrder());
    }

    private void showCustomDateTimePicker() {
        CustomDateTimePickerDialog dialog = CustomDateTimePickerDialog.newInstance(
                new CustomDateTimePickerDialog.OnDateTimeSelectedListener() {
                    @Override
                    public void onDateTimeSelected(String backendFormat, String displayFormat) {
                        selectedPickupTime = backendFormat; // For backend
                        selectedPickupDisplay = displayFormat; // For display

                        btnSelectTime.setText(displayFormat);
                        btnSelectTime.setTextColor(getResources().getColor(android.R.color.black));
                    }
                }
        );

        dialog.show(getSupportFragmentManager(), "custom_datetime_picker");
    }

    private void loadOrderData() {
        // Load customer data from session if available
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userJson = sp.getString("user", null);
        if (userJson != null) {
            try {
                com.example.waterrefilldraftv1.Customer.models.User u = new com.google.gson.Gson().fromJson(userJson, com.example.waterrefilldraftv1.Customer.models.User.class);
                if (u != null) {
                    tvCustomerName.setText((u.getFirstName()!=null?u.getFirstName():"") + " " + (u.getLastName()!=null?u.getLastName():""));
                    tvContactNumber.setText(u.getContactNo()!=null?u.getContactNo():"");
                }
            } catch (Exception ignored) {}
        }
        if (selectedAddress.isEmpty()) {
            tvSelectedAddress.setText("Select an address");
            tvSelectedAddress.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            tvSelectedAddress.setText(selectedAddress);
        }

        // Load cart items
        List<CartItem> cartItems = cartManager.getSelectedOrAllItems();
        if (!cartItems.isEmpty()) {
            CartItem firstItem = cartItems.get(0);
            ivGallonImage.setImageResource(firstItem.getProduct().getImageResource());

            StringBuilder details = new StringBuilder();
            double itemTotal = 0;
            for (CartItem item : cartItems) {
                details.append(item.getProduct().getType())
                        .append(" x")
                        .append(item.getQuantity())
                        .append(" - ")
                        .append(String.format("%.2f", item.getTotalPrice()))
                        .append("\n");
                itemTotal += item.getTotalPrice();
            }
            tvGallonInfo.setText(details.toString().trim());
            tvItemTotal.setText(String.format("%.2f", itemTotal));
        }

        double summaryTotal = 0;
        for (CartItem item : cartItems) summaryTotal += item.getTotalPrice();
        tvTotalAmount.setText("₱" + String.format("%.2f", summaryTotal));
        btnPaymentMethod.setText(selectedPaymentMethod);

        // Set placeholder text for pickup time
        btnSelectTime.setText("Select");
    }

    private void showPaymentMethodDialog() {
        String[] paymentMethods = {"Cash on Delivery", "PayPal"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Select Payment Method")
                .setItems(paymentMethods, (dialog, which) -> {
                    selectedPaymentMethod = paymentMethods[which];
                    btnPaymentMethod.setText(selectedPaymentMethod);
                });
        builder.show();
    }

    private void processOrder() {
        // Validate address
        if (selectedAddressId == null) {
            android.widget.Toast.makeText(this, "Please select an address", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if pickup time is selected
        if (selectedPickupTime.isEmpty()) {
            btnSelectTime.setText("Please select pickup time");
            btnSelectTime.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

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
            // Cash on delivery - send order to backend
            submitCodOrder();
        }
    }

    private void submitCodOrder() {
        List<CartItem> cartItems = cartManager.getSelectedOrAllItems();
        java.util.ArrayList<OrderRequest.OrderRequestItem> items = new java.util.ArrayList<>();
        for (CartItem item : cartItems) {
            items.add(new OrderRequest.OrderRequestItem(item.getProduct().getId(), item.getQuantity()));
        }

        OrderRequest request = new OrderRequest(items, selectedAddressId, selectedPickupTime, "COD");

        btnPlaceOrder.setEnabled(false);
        networkManager.placeOrder(request, new NetworkManager.ApiCallback<ApiResponse>() {
            @Override
            public void onSuccess(ApiResponse response) {
                btnPlaceOrder.setEnabled(true);
                if (response.isSuccess()) {
                    showOrderConfirmation();
                } else {
                    android.widget.Toast.makeText(OrderSummaryActivity.this, response.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                btnPlaceOrder.setEnabled(true);
                android.widget.Toast.makeText(OrderSummaryActivity.this, "Order failed: " + error, android.widget.Toast.LENGTH_LONG).show();
            }
        });
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
            selectedAddressId = data.getIntExtra("address_id", 0);
            tvSelectedAddress.setText(selectedAddress);
            tvSelectedAddress.setTextColor(getResources().getColor(android.R.color.black));
        }
    }
}