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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.Utils.ImageFormatter;
import com.example.waterrefilldraftv1.Riders.models.PickupOrder;
import com.example.waterrefilldraftv1.Riders.Utils.StatusFormatter;

import java.util.List;

public class PickupAdapter extends RecyclerView.Adapter<PickupAdapter.ViewHolder> {

    public interface OnPickupActionListener {
        void onViewDetails(PickupOrder order);
        void onMarkPickedUp(PickupOrder order);
    }

    private final List<PickupOrder> pickupList;
    private final OnPickupActionListener listener;

    public PickupAdapter(List<PickupOrder> pickupList, OnPickupActionListener listener) {
        this.pickupList = pickupList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rider_item_pickup_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        PickupOrder o = pickupList.get(pos);

        h.tvCustomerName.setText(o.getCustomerName() != null ? o.getCustomerName() : "N/A");
        h.tvAddress.setText(o.getAddress() != null ? o.getAddress() : "N/A");
        h.tvStatus.setText(StatusFormatter.format(o.getStatus()));
        h.tvGallonName.setText(o.getPrimaryGallonName());
        h.tvQuantity.setText("Qty: " + o.getPrimaryQuantity());

        // ✅ SIMPLE: Use ImageFormatter for safe image loading
        ImageFormatter.safeLoadGallonImage(
                h.ivGallon,
                o.getPrimaryImageUrl(),
                o.getPrimaryGallonName()
        );

        h.tvViewDetails.setOnClickListener(v -> listener.onViewDetails(o));
        h.btnMarkPickedUp.setOnClickListener(v -> {
            Log.d("PICKUP_ADAPTER", "🟢 MARK BUTTON CLICKED - Order: " + o.getOrderId() + ", Customer: " + o.getCustomerName());
            listener.onMarkPickedUp(o);
        });
        h.itemView.setOnClickListener(v -> listener.onViewDetails(o));
    }

    @Override
    public int getItemCount() {
        return pickupList == null ? 0 : pickupList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGallon;
        TextView tvCustomerName, tvAddress, tvStatus, tvViewDetails, tvGallonName, tvQuantity;
        Button btnMarkPickedUp;

        public ViewHolder(@NonNull View v) {
            super(v);
            ivGallon        = v.findViewById(R.id.iv_gallon_image);
            tvCustomerName  = v.findViewById(R.id.tv_customer_name);
            tvAddress       = v.findViewById(R.id.tv_address);
            tvStatus        = v.findViewById(R.id.tv_status);
            tvViewDetails   = v.findViewById(R.id.tv_view_details);
            tvGallonName    = v.findViewById(R.id.tv_gallon_name);
            tvQuantity      = v.findViewById(R.id.tv_quantity);
            btnMarkPickedUp = v.findViewById(R.id.btn_mark_picked_up);
        }
    }
}
