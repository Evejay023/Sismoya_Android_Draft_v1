package com.example.waterrefilldraftv1.Riders.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.Utils.ImageFormatter;
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

        // Set basic info only
        holder.tvCustomerName.setText(order.getCustomerName());
        holder.tvAddress.setText(order.getAddress());
        holder.tvGallonName.setText(order.getGallonName());
        // ❌ REMOVED: Quantity from item layout

        // Load image
        ImageFormatter.safeLoadGallonImage(
                holder.ivWaterIcon,
                order.getFullImageUrl(),
                order.getGallonName()
        );

        // Set click listeners
        holder.tvViewDetails.setOnClickListener(v -> {
            Log.d("DELIVERY_ADAPTER", "View details clicked for order: " + order.getOrderId());
            listener.onViewDetails(order);
        });

        holder.btnMarkDelivered.setOnClickListener(v -> {
            Log.d("DELIVERY_ADAPTER", "Mark delivered clicked for order: " + order.getOrderId());
            listener.onMarkDelivered(order);
        });

        // Optional: Add click listener to entire item
        holder.itemView.setOnClickListener(v -> {
            Log.d("DELIVERY_ADAPTER", "Item clicked for order: " + order.getOrderId());
            listener.onViewDetails(order);
        });
    }

    @Override
    public int getItemCount() {
        return deliveryList == null ? 0 : deliveryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Updated to match simplified layout
        public ImageView ivWaterIcon;
        public TextView tvCustomerName, tvAddress, tvViewDetails, tvGallonName;
        public Button btnMarkDelivered;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivWaterIcon = itemView.findViewById(R.id.iv_water_icon);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvViewDetails = itemView.findViewById(R.id.tv_view_details);
            tvGallonName = itemView.findViewById(R.id.tv_gallon_name);
            btnMarkDelivered = itemView.findViewById(R.id.btn_mark_delivered);

            // ❌ REMOVED: tvQuantity initialization
        }
    }

    public interface OnDeliverActionListener {
        void onMarkDelivered(RiderDelivery order);
        void onViewDetails(RiderDelivery order);
    }
}