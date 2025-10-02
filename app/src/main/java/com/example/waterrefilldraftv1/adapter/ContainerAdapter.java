package com.example.waterrefilldraftv1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.models.WaterContainer; // ✅ Use your model here

import java.util.List;

public class ContainerAdapter extends RecyclerView.Adapter<ContainerAdapter.ViewHolder> {

    private final List<WaterContainer> containerList; // ✅ Use model
    private final OnContainerClickListener listener;

    // Listener interface for handling container clicks
    public interface OnContainerClickListener {
        void onContainerClick(WaterContainer container); // ✅ Use model
    }

    public ContainerAdapter(@NonNull List<WaterContainer> containerList,
                            @NonNull OnContainerClickListener listener) {
        this.containerList = containerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WaterContainer container = containerList.get(position); // ✅ Use model

        holder.tvName.setText(container.getName());
        holder.tvCapacity.setText(container.getLiters());
        holder.tvPrice.setText(container.getPrice());
        holder.ivContainer.setImageResource(container.getImageResourceId());

        // Dim the view if not available
        holder.itemView.setAlpha(container.isAvailable() ? 1f : 0.6f);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContainerClick(container);
            }
        });
    }

    @Override
    public int getItemCount() {
        return containerList.size();
    }

    // ViewHolder holds the views for each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivContainer;
        final TextView tvName;
        final TextView tvCapacity;
        final TextView tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivContainer = itemView.findViewById(R.id.iv_container);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCapacity = itemView.findViewById(R.id.tv_capacity);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
