package com.example.waterrefilldraftv1.Customer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterrefilldraftv1.Customer.models.OrderOverview;
import com.example.waterrefilldraftv1.R;

import java.util.List;

public class DashboardOrderAdapter extends RecyclerView.Adapter<DashboardOrderAdapter.OrderViewHolder> {

    private final List<OrderOverview> orderList;
    private final Context context;

    public DashboardOrderAdapter(Context context, List<OrderOverview> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderOverview order = orderList.get(position);

        holder.tvOrderId.setText("Order ID: " + order.getOrderId());
        holder.tvStatus.setText("Status: " + order.getStatus());
        holder.tvAmount.setText("₱" + String.format("%.2f", order.getTotalAmount()));
        holder.tvDate.setText("Date: " + (order.getCreatedAt() != null ? order.getCreatedAt() : "N/A"));

        // 👇 Click to show dialog
        holder.btnViewDetails.setOnClickListener(v -> showOrderDetailsDialog(order));
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvAmount, tvDate;
        Button btnViewDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDate = itemView.findViewById(R.id.tv_date);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
        }
    }

    // 🔹 Dialog Popup (styled similar to your web version)
    private void showOrderDetailsDialog(OrderOverview order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.customer_dashboard_dialog_order_details, null);
        builder.setView(dialogView);

        TextView tvOrderId = dialogView.findViewById(R.id.tv_order_id);
        TextView tvPickupDate = dialogView.findViewById(R.id.tv_pickup_date);
        TextView tvGallonItems = dialogView.findViewById(R.id.tv_gallon_items);
        TextView tvTotalAmount = dialogView.findViewById(R.id.tv_total_amount);
        TextView tvPaymentMethod = dialogView.findViewById(R.id.tv_payment_method);
        Button btnClose = dialogView.findViewById(R.id.btn_close);

        // ✅ Use getters here instead of direct field access
        tvOrderId.setText("Order ID: " + order.getOrderId());
        tvPickupDate.setText("Pick Up DateTime: " + (order.getCreatedAt() != null ? order.getCreatedAt() : "N/A"));
        tvGallonItems.setText("Gallon: " + (order.getOrderItems() != null ? order.getOrderItems() : "N/A"));
        tvTotalAmount.setText("Total Amount: ₱" + String.format("%.2f", order.getTotalAmount()));
        tvPaymentMethod.setText("Payment Method: Cash"); // can be dynamic later

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
