package com.example.waterrefilldraftv1.Riders.UserInterrface.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.models.PickupOrder;
import com.example.waterrefilldraftv1.Riders.models.RiderDelivery;
import com.example.waterrefilldraftv1.Global.network.ApiService;
import com.example.waterrefilldraftv1.Global.network.RetrofitClient;
import com.example.waterrefilldraftv1.Global.network.ApiResponse;
import com.example.waterrefilldraftv1.Riders.Utils.ImageFormatter;
import com.example.waterrefilldraftv1.Riders.Utils.StatusFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

public class DashboardOrderDialog extends Dialog {

    private final PickupOrder order;
    private final String token;
    private final OnStatusUpdateListener listener;

    public interface OnStatusUpdateListener {
        void onStatusUpdated();
    }

    public DashboardOrderDialog(
            @NonNull Context context,
            PickupOrder order,
            String token,
            OnStatusUpdateListener listener
    ) {
        super(context);
        this.order = order;
        this.token = token;
        this.listener = listener;
        setupDialog();
    }

    private void setupDialog() {
        // Use the same layouts as your fragments
        if (order != null && "to_pickup".equals(order.getStatus())) {
            setContentView(R.layout.rider_dialog_pickup_details);
            setupPickupDialog();
        } else {
            setContentView(R.layout.rider_dialog_delivery_details);
            setupDeliveryDialog();
        }

        getWindow().setLayout(
                (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.9),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(true);
    }

    private void setupPickupDialog() {
        // Find all views from pickup dialog
        ImageView ivGallon = findViewById(R.id.iv_gallon_image);
        TextView tvGallonName = findViewById(R.id.tv_gallon_name);
        TextView tvGallonQuantity = findViewById(R.id.tv_gallon_quantity);
        TextView tvMoreItems = findViewById(R.id.tv_more_items);
        TextView tvOrderId = findViewById(R.id.tv_order_id);
        TextView tvCustomerName = findViewById(R.id.tv_customer_name);
        TextView tvContactNo = findViewById(R.id.tv_contact_no);
        TextView tvAddress = findViewById(R.id.tv_address);
        TextView tvPickupTime = findViewById(R.id.tv_pickup_time);
        TextView tvTotalAmount = findViewById(R.id.tv_total_amount);
        TextView tvPaymentMethod = findViewById(R.id.tv_payment_method);
        TextView tvOrderStatus = findViewById(R.id.tv_order_status);
        Button btnMarkPickedUp = findViewById(R.id.btn_mark_picked_up);
        ImageView close = findViewById(R.id.btn_close);

        if (order != null) {
            // Set order details
            tvOrderId.setText(String.valueOf(order.getOrderId()));
            tvCustomerName.setText(order.getCustomerName());
            tvContactNo.setText(order.getContactNumber());
            tvAddress.setText(order.getAddress());
            tvTotalAmount.setText(order.getFormattedTotal());
            tvPaymentMethod.setText(order.getPaymentMethod());
            tvOrderStatus.setText(StatusFormatter.format(order.getStatus()));

            // Set gallon details
            tvGallonName.setText(order.getPrimaryGallonName());
            tvGallonQuantity.setText("x" + order.getPrimaryQuantity());

            // Show "more items" if applicable
            if (order.hasMultipleGallons()) {
                tvMoreItems.setText("+" + (order.getItemCount() - 1) + " more items");
                tvMoreItems.setVisibility(View.VISIBLE);
            } else {
                tvMoreItems.setVisibility(View.GONE);
            }

            // Set pickup time
            if (tvPickupTime != null) {
                tvPickupTime.setText(order.getFormattedPickupDatetime());
            }

            // Load image
            ImageFormatter.safeLoadGallonImage(
                    ivGallon,
                    order.getPrimaryImageUrl(),
                    order.getPrimaryGallonName()
            );

            btnMarkPickedUp.setOnClickListener(v -> {
                updateOrderStatus(String.valueOf(order.getOrderId()), "picked_up", btnMarkPickedUp);
            });

            if (close != null) {
                close.setOnClickListener(v -> dismiss());
            }
        }
    }

    private void setupDeliveryDialog() {
        // Find all views from delivery dialog
        ImageView ivGallon = findViewById(R.id.iv_gallon_image);
        TextView tvGallonName = findViewById(R.id.tv_gallon_name);
        TextView tvGallonQuantity = findViewById(R.id.tv_gallon_quantity);
        TextView tvMoreItems = findViewById(R.id.tv_more_items);
        TextView tvOrderId = findViewById(R.id.tv_order_id);
        TextView tvCustomerName = findViewById(R.id.tv_customer_name);
        TextView tvContactNo = findViewById(R.id.tv_contact_no);
        TextView tvAddress = findViewById(R.id.tv_address);
        TextView tvTotalAmount = findViewById(R.id.tv_total_amount);
        TextView tvPaymentMethod = findViewById(R.id.tv_payment_method);
        TextView tvOrderStatus = findViewById(R.id.tv_order_status);
        Button btnMarkDelivered = findViewById(R.id.btn_mark_delivered);
        ImageView close = findViewById(R.id.btn_close);

        if (order != null) {
            // Set order details - using PickupOrder since that's what we have
            tvOrderId.setText(String.valueOf(order.getOrderId()));
            tvCustomerName.setText(order.getCustomerName());
            tvContactNo.setText(order.getContactNumber());
            tvAddress.setText(order.getAddress());
            tvTotalAmount.setText(order.getFormattedTotal());
            tvPaymentMethod.setText(order.getPaymentMethod());
            tvOrderStatus.setText(StatusFormatter.format(order.getStatus()));

            // Set gallon details using PickupOrder methods
            tvGallonName.setText(order.getPrimaryGallonName());
            tvGallonQuantity.setText("x" + order.getPrimaryQuantity());

            // Show "more items" if there are multiple items
            if (order.hasMultipleGallons()) {
                tvMoreItems.setText("+" + (order.getItemCount() - 1) + " more items");
                tvMoreItems.setVisibility(View.VISIBLE);
            } else {
                tvMoreItems.setVisibility(View.GONE);
            }

            // Load image using ImageFormatter
            ImageFormatter.safeLoadGallonImage(
                    ivGallon,
                    order.getPrimaryImageUrl(),
                    order.getPrimaryGallonName()
            );

            btnMarkDelivered.setOnClickListener(v -> {
                updateOrderStatus(String.valueOf(order.getOrderId()), "delivered", btnMarkDelivered);
            });

            if (close != null) {
                close.setOnClickListener(v -> dismiss());
            }
        }
    }

    private void updateOrderStatus(String orderId, String newStatus, Button button) {
        if (button != null) button.setEnabled(false);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        Map<String, String> body = new HashMap<>();
        body.put("newStatus", newStatus);

        String authHeader = "Bearer " + token;

        // ✅ FIXED: Remove Integer.parseInt() - pass orderId directly as String
        Call<ApiResponse> call = apiService.updateRiderOrderStatus(
                authHeader,
                orderId, // ✅ CHANGED: Pass String directly, no parsing needed
                body
        );

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (button != null) button.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Order updated successfully!", Toast.LENGTH_SHORT).show();
                    dismiss();

                    if (listener != null) listener.onStatusUpdated();
                } else {
                    Toast.makeText(getContext(), "Failed to update order status.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (button != null) button.setEnabled(true);
                Toast.makeText(getContext(), "Network error occurred.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}