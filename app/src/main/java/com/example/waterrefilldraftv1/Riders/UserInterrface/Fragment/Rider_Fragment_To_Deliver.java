package com.example.waterrefilldraftv1.Riders.UserInterrface.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.waterrefilldraftv1.Global.network.ApiResponse;
import com.example.waterrefilldraftv1.Global.network.ApiService;
import com.example.waterrefilldraftv1.Global.network.RetrofitClient;
import com.example.waterrefilldraftv1.Global.network.TokenManager;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.Adapter.RiderDeliveryAdapter;
import com.example.waterrefilldraftv1.Riders.models.PickupOrder;
import com.example.waterrefilldraftv1.Riders.models.Rider;
import com.example.waterrefilldraftv1.Riders.models.RiderDelivery;
import com.example.waterrefilldraftv1.Riders.models.RiderOrdersResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Rider_Fragment_To_Deliver extends Fragment {

    private static final String ARG_RIDER = "arg_rider";

    private Rider currentRider;
    private RecyclerView rvDeliveries;
    private RiderDeliveryAdapter adapter;
    private final List<RiderDelivery> deliveryList = new ArrayList<>();
    private ApiService apiService;

    // UI - Make them nullable to handle missing views
    private ProgressBar pbLoading;
    private View layoutEmpty;
    private TextView tvPending;

    public Rider_Fragment_To_Deliver() {}

    public static Rider_Fragment_To_Deliver newInstance(Rider rider) {
        Rider_Fragment_To_Deliver f = new Rider_Fragment_To_Deliver();
        Bundle b = new Bundle();
        b.putString(ARG_RIDER, new Gson().toJson(rider));
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getApiService();

        if (getArguments() != null) {
            currentRider = new Gson().fromJson(
                    getArguments().getString(ARG_RIDER),
                    Rider.class
            );
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.rider_fragment_to_deliver, container, false);

        // Initialize views with null checks
        rvDeliveries = view.findViewById(R.id.rv_delivery_orders);

        // ✅ FIXED: Initialize with null checks
        tvPending = view.findViewById(R.id.tv_pending_deliveries);
        pbLoading = view.findViewById(R.id.pb_loading);
        layoutEmpty = view.findViewById(R.id.layout_empty);

        // Log which views are found
        Log.d("DELIVERY_FRAGMENT", "rvDeliveries: " + (rvDeliveries != null));
        Log.d("DELIVERY_FRAGMENT", "tvPending: " + (tvPending != null));
        Log.d("DELIVERY_FRAGMENT", "pbLoading: " + (pbLoading != null));
        Log.d("DELIVERY_FRAGMENT", "layoutEmpty: " + (layoutEmpty != null));

        // Set up RecyclerView
        if (rvDeliveries != null) {
            rvDeliveries.setLayoutManager(new LinearLayoutManager(requireContext()));

            adapter = new RiderDeliveryAdapter(deliveryList, new RiderDeliveryAdapter.OnDeliverActionListener() {
                @Override
                public void onViewDetails(RiderDelivery order) {
                    showDeliveryDetailsDialog(requireContext(), order);
                }

                @Override
                public void onMarkDelivered(RiderDelivery order) {
                    markAsDelivered(order);
                }
            });

            rvDeliveries.setAdapter(adapter);
        } else {
            Log.e("DELIVERY_FRAGMENT", "RecyclerView not found in layout!");
        }

        fetchDeliveries();
        return view;
    }

    private void fetchDeliveries() {
        String token = TokenManager.getToken(requireContext());
        if (token == null) {
            Toast.makeText(requireContext(), "Session expired. Please login.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        apiService.getRiderOrders("Bearer " + token).enqueue(new Callback<RiderOrdersResponse>() {
            @Override
            public void onResponse(Call<RiderOrdersResponse> call, Response<RiderOrdersResponse> response) {
                showLoading(false);

                if (!isAdded()) return;

                if (!response.isSuccessful() || response.body() == null) {
                    showEmpty(true);
                    Toast.makeText(requireContext(), "Failed to load deliveries", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!response.body().isSuccess()) {
                    showEmpty(true);
                    Toast.makeText(requireContext(), "No delivery orders found", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<PickupOrder> allOrders = response.body().getData();
                deliveryList.clear();

                if (allOrders != null) {
                    for (PickupOrder order : allOrders) {
                        // ✅ FILTER: Only add valid orders with "to_deliver" status and customer data
                        if ("to_deliver".equalsIgnoreCase(order.getStatus()) &&
                                order.getCustomerName() != null &&
                                order.getItems() != null &&
                                !order.getItems().isEmpty()) {

                            // Convert PickupOrder to RiderDelivery
                            RiderDelivery delivery = new RiderDelivery(
                                    order.getOrderId(),
                                    order.getCustomerName(),
                                    order.getAddress(),
                                    order.getContactNumber(),
                                    order.getStatus(),
                                    order.getPaymentMethod(),
                                    order.getTotalPriceDouble(),
                                    order.getItems()
                            );
                            deliveryList.add(delivery);
                        }
                    }
                }

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                updatePendingCount();
                showEmpty(deliveryList.isEmpty());

                Log.d("DELIVERY_FRAGMENT", "Loaded " + deliveryList.size() + " valid delivery orders");
            }

            @Override
            public void onFailure(Call<RiderOrdersResponse> call, Throwable t) {
                showLoading(false);
                showEmpty(true);
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FETCH_DELIVERIES", "Error", t);
            }
        });
    }

    private void updatePendingCount() {
        if (tvPending != null) {
            tvPending.setText("Pending Deliveries: " + deliveryList.size());
        }
    }

    private void showLoading(boolean show) {
        // ✅ FIXED: Add null checks for all UI elements
        if (pbLoading != null) {
            pbLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvDeliveries != null) {
            rvDeliveries.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showEmpty(boolean show) {
        if (layoutEmpty != null) {
            layoutEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvDeliveries != null) {
            rvDeliveries.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void markAsDelivered(RiderDelivery order) {
        String token = TokenManager.getToken(requireContext());
        if (token == null) {
            Toast.makeText(requireContext(), "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("newStatus", "delivered");

        Toast.makeText(requireContext(), "Updating...", Toast.LENGTH_SHORT).show();

        apiService.updateRiderOrderStatus("Bearer " + token, order.getOrderId(), body)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), "✅ Marked as Delivered", Toast.LENGTH_SHORT).show();
                            fetchDeliveries(); // Refresh the list
                        } else {
                            Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show();
                            Log.e("MARK_DELIVERED", "Failed with code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                        Log.e("MARK_DELIVERED", "Error", t);
                    }
                });
    }

    private void showDeliveryDetailsDialog(Context ctx, RiderDelivery order) {
        Dialog d = new Dialog(ctx);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.rider_dialog_delivery_details);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.getWindow().setLayout((int)(getResources().getDisplayMetrics().widthPixels * 0.9),
                LayoutParams.WRAP_CONTENT);

        // Initialize dialog views
        TextView tvOrderId = d.findViewById(R.id.tv_order_id);
        ImageView iv = d.findViewById(R.id.iv_gallon_image);
        TextView tvCustomer = d.findViewById(R.id.tv_customer_name);
        TextView tvAddress = d.findViewById(R.id.tv_address);
        TextView tvQty = d.findViewById(R.id.tv_gallon_quantity);
        TextView tvMulti = d.findViewById(R.id.tv_more_items);
        Button btn = d.findViewById(R.id.btn_mark_delivered);
        ImageView close = d.findViewById(R.id.btn_close);

        // Set order data
        if (tvOrderId != null) {
            tvOrderId.setText("Order #" + order.getOrderId());
        }
        tvCustomer.setText(order.getCustomerName());
        tvAddress.setText(order.getAddress());
        tvQty.setText("x" + order.getGallonQuantity());

        if (order.hasMultipleItems()) {
            tvMulti.setText("+" + (order.getItemCount() - 1) + " more items");
            tvMulti.setVisibility(View.VISIBLE);
        } else {
            tvMulti.setVisibility(View.GONE);
        }

        // Load image
        if (order.getFullImageUrl() != null) {
            Glide.with(ctx)
                    .load(order.getFullImageUrl())
                    .placeholder(R.drawable.img_slim_container)
                    .into(iv);
        } else {
            iv.setImageResource(R.drawable.img_slim_container);
        }

        btn.setOnClickListener(v -> {
            markAsDelivered(order);
            d.dismiss();
        });

        close.setOnClickListener(v -> d.dismiss());
        d.show();
    }
}