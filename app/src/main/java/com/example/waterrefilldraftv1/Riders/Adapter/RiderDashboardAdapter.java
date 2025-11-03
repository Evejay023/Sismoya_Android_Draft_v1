package com.example.waterrefilldraftv1.Riders.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.models.PickupOrder;

import java.util.ArrayList;
import java.util.List;

public class RiderDashboardAdapter extends RecyclerView.Adapter<RiderDashboardAdapter.ViewHolder> {

    private final Context context;
    private final List<PickupOrder> filteredOrders = new ArrayList<>();
    private ViewDetailsListener viewListener;
    private MarkListener markListener;
    private final int layoutResId; // allow flexible layout name

    public RiderDashboardAdapter(Context context, int itemLayoutResId, ViewDetailsListener listener) {
        this.context = context;
        this.viewListener = listener;
        this.layoutResId = itemLayoutResId;
    }

    public interface ViewDetailsListener {
        void onView(PickupOrder order);
    }

    public interface MarkListener {
        void onMark(PickupOrder order);
    }

    public void setMarkListener(MarkListener listener) {
        this.markListener = listener;
    }

    public void setOrders(List<PickupOrder> allOrders, String category, int limit) {
        filteredOrders.clear();
        if (allOrders == null) {
            notifyDataSetChanged();
            return;
        }

        for (PickupOrder o : allOrders) {
            if ("to_pickup".equals(category) && "to_pickup".equals(o.getStatus())) {
                filteredOrders.add(o);
            } else if ("to_deliver".equals(category) && "to_deliver".equals(o.getStatus())) {
                filteredOrders.add(o);
            }
            if (filteredOrders.size() >= limit) break;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PickupOrder order = filteredOrders.get(position);

        holder.tvName.setText(order.getCustomerName() != null ? order.getCustomerName() : "N/A");
        holder.tvAddress.setText(order.getAddress() != null ? order.getAddress() : "N/A");
        holder.tvStatus.setText(order.getStatus() != null ? order.getStatus() : "");

        // ✅ Dynamic image from backend
        String img = order.getPrimaryImageUrl();
        String fullImg = null;

        if (img != null && !img.isEmpty()) {
            if (img.startsWith("http")) fullImg = img;
            else fullImg = "https://sismoya.bsit3b.site/" + img.replaceFirst("^/+", "");
        }

        if (fullImg != null) {
            Glide.with(holder.itemView.getContext())
                    .load(fullImg)
                    .placeholder(R.drawable.img_slim_container)
                    .error(R.drawable.img_sismoya_logo)
                    .into(holder.ivIcon);
        } else {
            holder.ivIcon.setImageResource(R.drawable.img_slim_container);
        }

        holder.tvViewDetails.setOnClickListener(v -> {
            if (viewListener != null) viewListener.onView(order);
        });

        holder.btnMark.setOnClickListener(v -> {
            if (markListener != null) markListener.onMark(order);
        });

        // status-based button text
        if ("to_pickup".equals(order.getStatus())) {
            holder.btnMark.setText("Mark as Picked-Up");
        } else if ("to_deliver".equals(order.getStatus())) {
            holder.btnMark.setText("Mark as Delivered");
        } else {
            holder.btnMark.setText("Mark");
        }
    }

    @Override
    public int getItemCount() {
        return filteredOrders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName, tvAddress, tvViewDetails, tvStatus;
        Button btnMark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_water_icon);
            if (ivIcon == null) ivIcon = itemView.findViewById(R.id.iv_gallon); // fallback
            tvName = itemView.findViewById(R.id.tv_customer_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvViewDetails = itemView.findViewById(R.id.tv_view_details);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnMark = itemView.findViewById(R.id.btn_mark_picked_up);
            if (btnMark == null) btnMark = itemView.findViewById(R.id.btn_mark); // fallback
        }
    }
}
