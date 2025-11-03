package com.example.waterrefilldraftv1.Riders.UserInterrface.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.models.PickupOrder;
import com.example.waterrefilldraftv1.Global.network.ApiService;
import com.example.waterrefilldraftv1.Global.network.RetrofitClient;
import com.example.waterrefilldraftv1.Global.network.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

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
        setContentView(R.layout.rider_dialog_order_details);
        getWindow().setLayout(
                (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.9),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        TextView tvOrderId = findViewById(R.id.tv_dialog_order_id);
        TextView tvOrderType = findViewById(R.id.tv_dialog_order_type);
        TextView tvCustomerName = findViewById(R.id.tv_dialog_customer_name);
        TextView tvContactNumber = findViewById(R.id.tv_dialog_contact_number);
        TextView tvAddress = findViewById(R.id.tv_dialog_address);
        TextView tvOrderStatus = findViewById(R.id.tv_dialog_order_status);
        TextView tvPickupDateTime = findViewById(R.id.tv_dialog_pickup_datetime);
        TextView tvCreatedAt = findViewById(R.id.tv_dialog_created_at);
        TextView tvPaymentMethod = findViewById(R.id.tv_dialog_payment_method);
        TextView tvTotalAmount = findViewById(R.id.tv_dialog_total_amount);
        Button btnUpdateStatus = findViewById(R.id.btn_dialog_update_status);

        if (order != null) {

            tvOrderId.setText("Order ID: " + order.getOrderId());

            String orderType = "to_pickup".equals(order.getStatus())
                    ? "Pickup Order"
                    : "Delivery Order";
            tvOrderType.setText(orderType);

            tvCustomerName.setText(order.getCustomerName() != null ? order.getCustomerName() : "N/A");
            tvContactNumber.setText(order.getContactNumber() != null ? order.getContactNumber() : "N/A");
            tvAddress.setText(order.getAddress() != null ? order.getAddress() : "N/A");

            String statusDisplay = "to_pickup".equals(order.getStatus())
                    ? "To Pick-Up"
                    : "To Deliver";
            tvOrderStatus.setText(statusDisplay);

            if (order.getPickupDatetime() != null && !order.getPickupDatetime().isEmpty()) {
                tvPickupDateTime.setText(order.getPickupDatetime());
            } else {
                tvPickupDateTime.setText("N/A");
            }

            tvCreatedAt.setText("N/A");

            tvPaymentMethod.setText(order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A");
            tvTotalAmount.setText("₱ " + String.format(Locale.getDefault(), "%.2f", order.getTotalPriceDouble()));

            String newStatus = "to_pickup".equals(order.getStatus())
                    ? "picked_up"
                    : "delivered";

            btnUpdateStatus.setText(newStatus.equals("picked_up") ? "Mark as Picked Up" : "Mark as Delivered");

            btnUpdateStatus.setOnClickListener(v ->
                    updateOrderStatus(String.valueOf(order.getOrderId()), newStatus, btnUpdateStatus)
            );
        }

        setCanceledOnTouchOutside(true);
    }

    public void updateImmediately() {
        String newStatus = order.getStatus().equals("to_pickup") ? "picked_up" : "delivered";
        updateOrderStatus(String.valueOf(order.getOrderId()), newStatus, null);
    }

    private void updateOrderStatus(String orderId, String newStatus, Button button) {
        if (button != null) button.setEnabled(false);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        Map<String, String> body = new HashMap<>();
        body.put("newStatus", newStatus);

        String authHeader = "Bearer " + token;

        Call<ApiResponse> call = apiService.updateRiderOrderStatus(
                authHeader,
                Integer.parseInt(orderId),
                body
        );

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (button != null) button.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Order updated successfully.", Toast.LENGTH_SHORT).show();
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
