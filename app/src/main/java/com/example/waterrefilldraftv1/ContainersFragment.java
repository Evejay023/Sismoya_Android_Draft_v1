package com.example.waterrefilldraftv1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * ContainersFragment - Product listing screen
 * Shows available water containers with Add to Cart and Order Now options
 */
public class ContainersFragment extends Fragment {

    private RecyclerView recyclerViewContainers;
    private ContainerOrderAdapter adapter;
    private List<WaterContainer> containerList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_containers, container, false);

        initViews(view);
        setupRecyclerView();

        return view;
    }

    private void initViews(View view) {
        recyclerViewContainers = view.findViewById(R.id.recycler_view_containers);
    }

    private void setupRecyclerView() {
        containerList = createContainerList();
        adapter = new ContainerOrderAdapter(containerList, this::onAddToCartClick, this::onOrderNowClick);

        recyclerViewContainers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewContainers.setAdapter(adapter);
    }

    private List<WaterContainer> createContainerList() {
        List<WaterContainer> containers = new ArrayList<>();

        // Round Container
        containers.add(new WaterContainer(
                1, "Round Container", "50", "30.00",
                R.drawable.round_container, true
        ));

        // Slim Container
        containers.add(new WaterContainer(
                2, "Slim Container", "50", "30.00",
                R.drawable.slim_container, true
        ));

        // Mini Container
        containers.add(new WaterContainer(
                3, "Mini Container", "25", "15.00",
                R.drawable.mini_container, true
        ));

        return containers;
    }

    private void onAddToCartClick(WaterContainer container) {
        // Show add to cart popup
        AddToCartDialog dialog = AddToCartDialog.newInstance(container);
        dialog.show(getParentFragmentManager(), "add_to_cart");
    }

    private void onOrderNowClick(WaterContainer container) {
        // Navigate directly to order summary
        OrderSummaryDialog dialog = OrderSummaryDialog.newInstance(container, 1);
        dialog.show(getParentFragmentManager(), "order_summary");
    }

    /**
     * Water Container model class
     */
    public static class WaterContainer {
        private int id;
        private String name;
        private String liters;
        private String price;
        private int imageResourceId;
        private boolean available;

        public WaterContainer(int id, String name, String liters, String price, int imageResourceId, boolean available) {
            this.id = id;
            this.name = name;
            this.liters = liters;
            this.price = price;
            this.imageResourceId = imageResourceId;
            this.available = available;
        }

        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getLiters() { return liters; }
        public String getPrice() { return price; }
        public int getImageResourceId() { return imageResourceId; }
        public boolean isAvailable() { return available; }
    }
}