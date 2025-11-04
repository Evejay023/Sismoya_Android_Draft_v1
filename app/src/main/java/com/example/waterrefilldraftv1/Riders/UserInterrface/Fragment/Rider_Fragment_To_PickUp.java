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
import com.example.waterrefilldraftv1.Riders.Adapter.PickupAdapter;
import com.example.waterrefilldraftv1.Riders.models.PickupOrder;
import com.example.waterrefilldraftv1.Riders.models.Rider;
import com.example.waterrefilldraftv1.Riders.models.RiderOrdersResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Rider_Fragment_To_PickUp extends Fragment {

    private static final String ARG_RIDER = "arg_rider";

    private Rider currentRider;
    private RecyclerView rvPickups;
    private PickupAdapter adapter;
    private final List<PickupOrder> pickupList = new ArrayList<>();
    private ApiService apiService;

    // UI
    private ProgressBar pbLoading;
    private View layoutEmpty;
    private TextView tvPending;

    public Rider_Fragment_To_PickUp() {}

    public static Rider_Fragment_To_PickUp newInstance(Rider rider) {
        Rider_Fragment_To_PickUp f = new Rider_Fragment_To_PickUp();
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

        View view = inflater.inflate(R.layout.rider_fragment_to_pickup, container, false);

        rvPickups = view.findViewById(R.id.rv_pickup_orders);
        rvPickups.setLayoutManager(new LinearLayoutManager(requireContext()));
        tvPending = view.findViewById(R.id.tv_pending_pickups);
        pbLoading = view.findViewById(R.id.pb_loading);
        layoutEmpty = view.findViewById(R.id.layout_empty);

        adapter = new PickupAdapter(pickupList, new PickupAdapter.OnPickupActionListener() {
            @Override
            public void onViewDetails(PickupOrder order) {
                showPickupDetailsDialog(requireContext(), order);
            }

            @Override
            public void onMarkPickedUp(PickupOrder order) {
                markAsPickedUp(order);
            }
        });

        rvPickups.setAdapter(adapter);

        fetchPickups();
        return view;
    }

    private void fetchPickups() {
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
                    Toast.makeText(requireContext(), "Failed to load pick ups", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!response.body().isSuccess()) {
                    showEmpty(true);
                    Toast.makeText(requireContext(), "No pickup orders found", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<PickupOrder> allOrders = response.body().getData();
                pickupList.clear();

                if (allOrders != null) {
                    for (PickupOrder order : allOrders) {
                        // ✅ FILTER: Only add valid orders with customer data
                        if ("to_pickup".equalsIgnoreCase(order.getStatus()) &&
                                order.getCustomerName() != null &&
                                order.getItems() != null &&
                                !order.getItems().isEmpty()) {

                            pickupList.add(order);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                updatePendingCount();
                showEmpty(pickupList.isEmpty());

                Log.d("PICKUP_FRAGMENT", "Loaded " + pickupList.size() + " valid pickup orders");
            }

            @Override
            public void onFailure(Call<RiderOrdersResponse> call, Throwable t) {
                showLoading(false);
                showEmpty(true);
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FETCH_PICKUPS", "Error", t);
            }
        });
    }


    private void updatePendingCount() {
        tvPending.setText("Pending Pick-Up: " + pickupList.size());
    }

    private void showLoading(boolean show) {
        pbLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        rvPickups.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmpty(boolean show) {
        layoutEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void markAsPickedUp(PickupOrder order) {
        String token = TokenManager.getToken(requireContext());
        if (token == null) {
            Toast.makeText(requireContext(), "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("newStatus", "picked_up");

        Toast.makeText(requireContext(), "Updating...", Toast.LENGTH_SHORT).show();

        // ✅ FIXED: order.getOrderId() returns String
        apiService.updateRiderOrderStatus("Bearer " + token, order.getOrderId(), body)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful()) {
                            // ✅ SUCCESS - Always refresh the list when we get 200 response
                            Toast.makeText(requireContext(), "✅ Marked as Picked Up", Toast.LENGTH_SHORT).show();
                            fetchPickups(); // This will reload fresh data from server
                        } else {
                            Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showPickupDetailsDialog(Context ctx, PickupOrder order) {
        Dialog d = new Dialog(ctx);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.rider_dialog_pickup_details);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.getWindow().setLayout((int)(getResources().getDisplayMetrics().widthPixels * 0.9),
                LayoutParams.WRAP_CONTENT);

        ImageView iv = d.findViewById(R.id.iv_gallon_image);
        TextView tvCustomer = d.findViewById(R.id.tv_customer_name);
        TextView tvQty = d.findViewById(R.id.tv_gallon_quantity);
        TextView tvMulti = d.findViewById(R.id.tv_more_items);
        Button btn = d.findViewById(R.id.btn_mark_picked_up);
        ImageView close = d.findViewById(R.id.btn_close);

        tvCustomer.setText(order.getCustomerName());
        tvQty.setText("x" + order.getPrimaryQuantity());

        if (order.hasMultipleGallons()) {
            tvMulti.setVisibility(View.VISIBLE);
        }

        Glide.with(ctx)
                .load(order.getPrimaryImageUrl())
                .placeholder(R.drawable.img_slim_container)
                .into(iv);

        btn.setOnClickListener(v -> {
            markAsPickedUp(order);
            d.dismiss();
        });

        close.setOnClickListener(v -> d.dismiss());
        d.show();
    }
}
