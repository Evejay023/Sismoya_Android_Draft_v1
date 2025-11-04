package com.example.waterrefilldraftv1.Riders.UserInterrface.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Global.network.RetrofitClient;
import com.example.waterrefilldraftv1.Riders.Adapter.CompletedOrdersAdapter;
import com.example.waterrefilldraftv1.Riders.models.CompletedOrderModel;
import com.example.waterrefilldraftv1.Global.network.ApiService;
import com.example.waterrefilldraftv1.Riders.models.Rider;
import com.example.waterrefilldraftv1.Riders.Utils.ImageFormatter;
import com.example.waterrefilldraftv1.Riders.Utils.StatusFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Rider_Fragment_Delivery_History extends Fragment {

    private static final String ARG_RIDER = "rider_data";

    private RecyclerView rvCompletedOrders;
    private CompletedOrdersAdapter adapter;
    private List<CompletedOrderModel> orderList = new ArrayList<>();
    private ApiService apiService;
    private Rider currentRider;

    public static Rider_Fragment_Delivery_History newInstance(Rider rider) {
        Rider_Fragment_Delivery_History fragment = new Rider_Fragment_Delivery_History();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RIDER, rider);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            currentRider = (Rider) getArguments().getSerializable(ARG_RIDER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.rider_fragment_delivery_history, container, false);

        rvCompletedOrders = view.findViewById(R.id.rv_completed_orders);
        rvCompletedOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CompletedOrdersAdapter(orderList);
        rvCompletedOrders.setAdapter(adapter);

        apiService = RetrofitClient.getApiService();

        loadCompletedOrders();

        return view;
    }

    private void loadCompletedOrders() {
        // Show loading state if you have a ProgressBar
        // progressBar.setVisibility(View.VISIBLE);

        apiService.getDeliveryHistory().enqueue(new Callback<List<CompletedOrderModel>>() {
            @Override
            public void onResponse(Call<List<CompletedOrderModel>> call, Response<List<CompletedOrderModel>> response) {
                // Hide loading state
                // progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<CompletedOrderModel> orders = response.body();
                    adapter.updateOrders(orders);
                    Log.d("DELIVERY_HISTORY", "Loaded " + orders.size() + " completed orders");

                    // Show empty state if no orders
                    if (orders.isEmpty()) {
                        // Show empty view
                        // tvEmptyState.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "No delivery history found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch history", Toast.LENGTH_SHORT).show();
                    Log.e("DELIVERY_HISTORY", "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<CompletedOrderModel>> call, Throwable t) {
                // Hide loading state
                // progressBar.setVisibility(View.GONE);

                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DELIVERY_HISTORY", "Error loading orders", t);
            }
        });
    }
}
