package com.example.waterrefilldraftv1.Riders.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.Utils.StatusFormatter;
import com.example.waterrefilldraftv1.Riders.models.RiderDelivery;

import java.util.List;

public class RiderDeliveryAdapter extends RecyclerView.Adapter<RiderDeliveryAdapter.ViewHolder> {

    private final List<RiderDelivery> deliveryList;
    private final OnDeliverActionListener listener;

    public RiderDeliveryAdapter(List<RiderDelivery> deliveryList, OnDeliverActionListener listener) {
        this.deliveryList = deliveryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rider_item_delivery_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RiderDelivery order = deliveryList.get(position);

        holder.tvCustomerName.setText(order.getCustomerName());
        holder.tvAddress.setText(order.getAddress());
        holder.tvStatus.setText(StatusFormatter.format(order.getStatus()));

        holder.tvViewDetails.setOnClickListener(v -> listener.onViewDetails(order));
        holder.btnMarkDelivered.setOnClickListener(v -> listener.onMarkDelivered(order));
    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvAddress, tvStatus, tvViewDetails;
        Button btnMarkDelivered;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvViewDetails = itemView.findViewById(R.id.tv_view_details);
            btnMarkDelivered = itemView.findViewById(R.id.btn_mark_delivered);
        }
    }

    public interface OnDeliverActionListener {
        void onMarkDelivered(RiderDelivery order);
        void onViewDetails(RiderDelivery order);
    }
}
