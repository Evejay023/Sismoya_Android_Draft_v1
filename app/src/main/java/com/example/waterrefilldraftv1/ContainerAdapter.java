package com.example.waterrefilldraftv1;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * ContainerAdapter - RecyclerView adapter for water containers
 * Displays water container items in a grid layout
 * Handles item clicks and availability status
 */
public class ContainerAdapter extends RecyclerView.Adapter<ContainerAdapter.ContainerViewHolder> {

    private List<ContainersFragment.WaterContainer> containerList;
    private OnContainerClickListener clickListener;

    /**
     * Interface for handling container item clicks
     */
    public interface OnContainerClickListener {
        void onContainerClick(ContainersFragment.WaterContainer container);
    }

    /**
     * Constructor
     * @param containerList List of water containers
     * @param clickListener Click listener for container items
     */
    public ContainerAdapter(List<ContainersFragment.WaterContainer> containerList, OnContainerClickListener clickListener) {
        this.containerList = containerList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_container, parent, false);
        return new ContainerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContainerViewHolder holder, int position) {
        ContainersFragment.WaterContainer container = containerList.get(position);
        holder.bind(container, clickListener);
    }

    @Override
    public int getItemCount() {
        return containerList.size();
    }

    /**
     * ViewHolder class for container items
     */
    public static class ContainerViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivContainer, ivAvailability;
        private TextView tvName, tvCapacity, tvPrice, tvStatus;

        public ContainerViewHolder(@NonNull View itemView) {
            super(itemView);

            ivContainer = itemView.findViewById(R.id.iv_container);
            ivAvailability = itemView.findViewById(R.id.iv_availability);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCapacity = itemView.findViewById(R.id.tv_capacity);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        /**
         * Bind container data to view elements
         * @param container Water container object
         * @param clickListener Click listener for the item
         */
        public void bind(ContainersFragment.WaterContainer container, OnContainerClickListener clickListener) {
            tvName.setText(container.getName());
            tvCapacity.setText(container.getLiters());
            tvPrice.setText(container.getPrice());

            ivContainer.setImageResource(container.getImageResourceId());

            if (container.isAvailable()) {
                tvStatus.setText("Available");
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                ivAvailability.setImageResource(R.drawable.ic_check_circle);
                itemView.setAlpha(1.0f);
            } else {
                tvStatus.setText("Out of Stock");
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                ivAvailability.setImageResource(R.drawable.ic_cancel);
                itemView.setAlpha(0.6f);
            }

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onContainerClick(container);
                }
            });
        }
    }
}