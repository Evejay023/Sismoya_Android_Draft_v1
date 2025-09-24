package com.example.waterrefilldraftv1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterrefilldraftv1.ui.s.fragment.ContainersFragment;
import com.example.waterrefilldraftv1.R;

import java.util.List;

public class ContainerAdapter extends RecyclerView.Adapter<ContainerAdapter.ViewHolder> {

    private List<ContainersFragment.WaterContainer> containerList;
    private OnContainerClickListener listener;

    public interface OnContainerClickListener {
        void onContainerClick(ContainersFragment.WaterContainer container);
    }

    public ContainerAdapter(List<ContainersFragment.WaterContainer> containerList, OnContainerClickListener listener) {
        this.containerList = containerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContainersFragment.WaterContainer container = containerList.get(position);
        holder.tvName.setText(container.getName());
        holder.tvCapacity.setText(container.getLiters());
        holder.tvPrice.setText(container.getPrice());
        holder.ivContainer.setImageResource(container.getImageResourceId());
        holder.itemView.setAlpha(container.isAvailable() ? 1f : 0.6f);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onContainerClick(container);
        });
    }

    @Override
    public int getItemCount() {
        return containerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivContainer;
        TextView tvName, tvCapacity, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivContainer = itemView.findViewById(R.id.iv_container);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCapacity = itemView.findViewById(R.id.tv_capacity);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
