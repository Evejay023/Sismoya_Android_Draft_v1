package com.example.waterrefilldraftv1.Riders.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.models.PickupOrder;
import com.example.waterrefilldraftv1.Riders.Utils.StatusFormatter;


import java.util.List;

public class PickupAdapter extends RecyclerView.Adapter<PickupAdapter.ViewHolder> {

    private final List<PickupOrder> pickupList;
    private final OnPickupActionListener listener;

    public PickupAdapter(List<PickupOrder> pickupList, OnPickupActionListener listener) {
        this.pickupList = pickupList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rider_item_pickup_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PickupOrder order = pickupList.get(position);

        // Set text safely with fallback to avoid "null" or "N/A"
        holder.tvCustomerName.setText(order.getCustomerName() != null ? order.getCustomerName() : "N/A");
        holder.tvAddress.setText(order.getAddress() != null ? order.getAddress() : "N/A");
        holder.tvStatus.setText(
                order.getStatus() != null
                        ? StatusFormatter.format(order.getStatus())
                        : "N/A"
        );

        // Click listeners
        holder.tvViewDetails.setOnClickListener(v -> listener.onViewDetails(order));
        holder.btnMarkPickup.setOnClickListener(v -> listener.onMarkPickedUp(order));
    }


    @Override
    public int getItemCount() {
        return pickupList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvAddress, tvPickupTime, tvStatus, tvViewDetails;
        Button btnMarkPickup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvPickupTime = itemView.findViewById(R.id.tv_pickup_time); // optional
            tvStatus = itemView.findViewById(R.id.tv_status); // optional if in layout
            tvViewDetails = itemView.findViewById(R.id.tv_view_details);
            btnMarkPickup = itemView.findViewById(R.id.btn_mark_picked_up);
        }
    }

    public interface OnPickupActionListener {
        void onMarkPickedUp(PickupOrder order);
        void onViewDetails(PickupOrder order);
    }
}
