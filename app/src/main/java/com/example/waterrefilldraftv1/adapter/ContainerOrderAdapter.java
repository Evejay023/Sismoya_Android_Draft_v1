package com.example.waterrefilldraftv1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterrefilldraftv1.ui.s.fragment.ContainersFragment;
import com.example.waterrefilldraftv1.R;

import java.util.List;

public class ContainerOrderAdapter extends RecyclerView.Adapter<ContainerOrderAdapter.ViewHolder> {

    private List<ContainersFragment.WaterContainer> containerList;
    private OnAddToCartClickListener addToCartListener;
    private OnOrderNowClickListener orderNowListener;

    public interface OnAddToCartClickListener {
        void onAddToCartClick(ContainersFragment.WaterContainer container);
    }

    public interface OnOrderNowClickListener {
        void onOrderNowClick(ContainersFragment.WaterContainer container);
    }

    public ContainerOrderAdapter(List<ContainersFragment.WaterContainer> containerList,
                                 OnAddToCartClickListener addToCartListener,
                                 OnOrderNowClickListener orderNowListener) {
        this.containerList = containerList;
        this.addToCartListener = addToCartListener;
        this.orderNowListener = orderNowListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_container_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContainersFragment.WaterContainer container = containerList.get(position);
        holder.bind(container, addToCartListener, orderNowListener);
    }

    @Override
    public int getItemCount() {
        return containerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivContainer;
        private TextView tvContainerType, tvContainerLiters, tvContainerPrice;
        private Button btnAddToCart, btnOrderNow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivContainer = itemView.findViewById(R.id.iv_container);
            tvContainerType = itemView.findViewById(R.id.tv_container_type);
            tvContainerLiters = itemView.findViewById(R.id.tv_container_liters);
            tvContainerPrice = itemView.findViewById(R.id.tv_container_price);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
            btnOrderNow = itemView.findViewById(R.id.btn_order_now);
        }

        public void bind(ContainersFragment.WaterContainer container,
                         OnAddToCartClickListener addToCartListener,
                         OnOrderNowClickListener orderNowListener) {

            tvContainerType.setText("Type: " + container.getName());
            tvContainerLiters.setText("Liters: " + container.getLiters());
            tvContainerPrice.setText("Price: " + container.getPrice());
            ivContainer.setImageResource(container.getImageResourceId());

            btnAddToCart.setOnClickListener(v -> {
                if (addToCartListener != null) {
                    addToCartListener.onAddToCartClick(container);
                }
            });

            btnOrderNow.setOnClickListener(v -> {
                if (orderNowListener != null) {
                    orderNowListener.onOrderNowClick(container);
                }
            });
        }
    }
}