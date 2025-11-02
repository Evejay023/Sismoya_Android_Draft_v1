package com.example.waterrefilldraftv1.Riders.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.models.CompletedOrderModel;

import java.util.List;

public class CompletedOrdersAdapter extends RecyclerView.Adapter<CompletedOrdersAdapter.ViewHolder> {

    private List<CompletedOrderModel> orders;

    public CompletedOrdersAdapter(List<CompletedOrderModel> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rider_item_completed_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CompletedOrderModel order = orders.get(position);

        holder.tvCustomerName.setText(order.getCustomer_name());
        holder.tvDeliveryTime.setText(order.getDelivered_at());
        holder.tvAddress.setText(order.getAddress());

        holder.tvViewDetails.setOnClickListener(v -> {
            showCompletedOrderDialog(v.getContext(), order);
        });
    }

    private void showCompletedOrderDialog(Context context, CompletedOrderModel order) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.rider_dialog_completed_order);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvOrderId = dialog.findViewById(R.id.tv_order_id);
        TextView tvCustomerName = dialog.findViewById(R.id.tv_customer_name);
        TextView tvContactNo = dialog.findViewById(R.id.tv_contact_no);
        TextView tvAddress = dialog.findViewById(R.id.tv_address);
        TextView tvTotalAmount = dialog.findViewById(R.id.tv_total_amount);
        TextView tvPaymentMethod = dialog.findViewById(R.id.tv_payment_method);
        TextView tvOrderStatus = dialog.findViewById(R.id.tv_order_status);
        TextView tvDeliveredAt = dialog.findViewById(R.id.tv_delivered_at);

        ImageView btnClose = dialog.findViewById(R.id.btn_close);

        tvOrderId.setText(String.valueOf(order.getOrder_id()));
        tvCustomerName.setText(order.getCustomer_name());
        tvContactNo.setText(order.getContact_no());
        tvAddress.setText(order.getAddress());
        tvTotalAmount.setText(order.getTotal_price());
        tvPaymentMethod.setText(order.getPayment_method());
        tvOrderStatus.setText(order.getStatus());
        tvDeliveredAt.setText(order.getDelivered_at());

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCustomerName, tvDeliveryTime, tvAddress, tvViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvDeliveryTime = itemView.findViewById(R.id.tv_delivery_time);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvViewDetails = itemView.findViewById(R.id.tv_view_details);
        }
    }
}
