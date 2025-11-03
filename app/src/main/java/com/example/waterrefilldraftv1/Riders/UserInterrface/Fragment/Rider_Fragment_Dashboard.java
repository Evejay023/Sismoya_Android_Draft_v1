package com.example.waterrefilldraftv1.Riders.UserInterrface.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterrefilldraftv1.Global.network.ApiResponse;
import com.example.waterrefilldraftv1.Global.network.ApiService;
import com.example.waterrefilldraftv1.Global.network.RetrofitClient;
import com.example.waterrefilldraftv1.Global.network.TokenManager;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.Adapter.PickupAdapter;
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

public class Rider_Fragment_Dashboard extends Fragment {

    private static final String ARG_RIDER = "arg_rider";
    private Rider rider;

    private TextView tvRiderName, tvEmptyPickup, tvEmptyDelivery;
    private RecyclerView rvDashboardPickups, rvDashboardDeliveries;

    private PickupAdapter pickupAdapter;
    private RiderDeliveryAdapter deliveryAdapter;

    private ApiService apiService;

    private List<PickupOrder> allPickup = new ArrayList<>();
    private List<RiderDelivery> allDelivery = new ArrayList<>();

    private Handler handler = new Handler();
    private final int REFRESH_INTERVAL = 5000; // 5 seconds

    public static Rider_Fragment_Dashboard newInstance(Rider rider) {
        Rider_Fragment_Dashboard fragment = new Rider_Fragment_Dashboard();
        Bundle args = new Bundle();
        args.putString(ARG_RIDER, new Gson().toJson(rider));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = RetrofitClient.getApiService();

        if (getArguments() != null) {
            rider = new Gson().fromJson(getArguments().getString(ARG_RIDER), Rider.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rider_fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvRiderName = view.findViewById(R.id.tv_rider_name);
        tvEmptyPickup = view.findViewById(R.id.tv_empty_pickup);
        tvEmptyDelivery = view.findViewById(R.id.tv_empty_delivery);

        rvDashboardPickups = view.findViewById(R.id.rv_dashboard_pickups);
        rvDashboardDeliveries = view.findViewById(R.id.rv_dashboard_deliveries);

        rvDashboardPickups.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDashboardDeliveries.setLayoutManager(new LinearLayoutManager(getContext()));

        if (rider != null) {
            tvRiderName.setText(rider.getFullName());
        }

        fetchOrders();
    }

    private void fetchOrders() {
        String token = TokenManager.getToken(requireContext());
        if (token == null) return;

        apiService.getRiderOrders("Bearer " + token)
                .enqueue(new Callback<RiderOrdersResponse>() {
                    @Override
                    public void onResponse(Call<RiderOrdersResponse> call,
                                           Response<RiderOrdersResponse> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(requireContext(), "Dashboard fetch failed", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<PickupOrder> all = response.body().getData();

                        allPickup.clear();
                        allDelivery.clear();

                        for (PickupOrder o : all) {
                            String s = o.getStatus().toLowerCase();

                            if (s.equals("to_pickup")) {
                                allPickup.add(o);
                            } else if (s.equals("to_deliver")) {
                                allDelivery.add(new RiderDelivery(
                                        o.getOrderId(),
                                        o.getCustomerName(),
                                        o.getAddress(),
                                        o.getContactNumber(),
                                        o.getStatus(),
                                        o.getPaymentMethod(),
                                        o.getTotalAmount()
                                ));
                            }
                        }

                        bindAdapters();
                        updateEmptyState();
                    }

                    @Override
                    public void onFailure(Call<RiderOrdersResponse> call, Throwable t) {
                        Toast.makeText(requireContext(), "Network dashboard error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void bindAdapters() {

        List<PickupOrder> limitPickup = allPickup.subList(0, Math.min(3, allPickup.size()));
        List<RiderDelivery> limitDelivery = allDelivery.subList(0, Math.min(3, allDelivery.size()));

        pickupAdapter = new PickupAdapter(limitPickup, new PickupAdapter.OnPickupActionListener() {
            @Override
            public void onMarkPickedUp(PickupOrder order) {
                markAsPickedUp(order);
            }

            @Override
            public void onViewDetails(PickupOrder order) {
                showPickupDetailsDialog(requireContext(), order);
            }
        });

        deliveryAdapter = new RiderDeliveryAdapter(limitDelivery, new RiderDeliveryAdapter.OnDeliverActionListener() {
            @Override
            public void onMarkDelivered(RiderDelivery order) {
                markAsDelivered(order);
            }

            @Override
            public void onViewDetails(RiderDelivery order) {
                showDeliveryDetailsDialog(requireContext(), order);
            }
        });

        rvDashboardPickups.setAdapter(pickupAdapter);
        rvDashboardDeliveries.setAdapter(deliveryAdapter);
    }

    private void updateEmptyState() {
        tvEmptyPickup.setVisibility(allPickup.isEmpty() ? View.VISIBLE : View.GONE);
        tvEmptyDelivery.setVisibility(allDelivery.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // Update status -----
    private void markAsPickedUp(PickupOrder order) {
        String token = TokenManager.getToken(requireContext());
        if (token == null) return;

        Map<String, String> body = new HashMap<>();
        body.put("newStatus", "picked_up");

        apiService.updateRiderOrderStatus("Bearer " + token, order.getOrderId(), body)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        fetchOrders();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) { }
                });
    }

    private void markAsDelivered(RiderDelivery order) {
        String token = TokenManager.getToken(requireContext());
        if (token == null) return;

        Map<String, String> body = new HashMap<>();
        body.put("newStatus", "delivered");

        apiService.updateRiderOrderStatus("Bearer " + token, order.getOrderId(), body)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        fetchOrders();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) { }
                });
    }

    // Dialogs -----
    private void showPickupDetailsDialog(Context ctx, PickupOrder order) {
        Dialog d = new Dialog(ctx);
        d.setContentView(R.layout.rider_dialog_delivery_details);
        if (d.getWindow() != null) d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvOrderId = d.findViewById(R.id.tv_order_id);
        TextView tvCustomerName = d.findViewById(R.id.tv_customer_name);
        TextView tvContactNo = d.findViewById(R.id.tv_contact_no);
        TextView tvAddress = d.findViewById(R.id.tv_address);
        TextView tvTotalAmount = d.findViewById(R.id.tv_total_amount);
        TextView tvPaymentMethod = d.findViewById(R.id.tv_payment_method);
        TextView tvOrderStatus = d.findViewById(R.id.tv_order_status);
        Button btnMark = d.findViewById(R.id.btn_mark_delivered);
        ImageView close = d.findViewById(R.id.btn_close);

        tvOrderId.setText(String.valueOf(order.getOrderId()));
        tvCustomerName.setText(order.getCustomerName());
        tvContactNo.setText(order.getContactNumber());
        tvAddress.setText(order.getAddress());
        tvTotalAmount.setText(String.format("%.2f", order.getTotalAmount()));
        tvPaymentMethod.setText(order.getPaymentMethod());
        tvOrderStatus.setText(order.getStatus());

        btnMark.setText("Mark as Picked Up");
        btnMark.setOnClickListener(v -> {
            markAsPickedUp(order);
            d.dismiss();
        });

        close.setOnClickListener(v -> d.dismiss());
        d.show();
    }

    private void showDeliveryDetailsDialog(Context ctx, RiderDelivery order) {
        Dialog d = new Dialog(ctx);
        d.setContentView(R.layout.rider_dialog_delivery_details);
        if (d.getWindow() != null) d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvOrderId = d.findViewById(R.id.tv_order_id);
        TextView tvCustomerName = d.findViewById(R.id.tv_customer_name);
        TextView tvContactNo = d.findViewById(R.id.tv_contact_no);
        TextView tvAddress = d.findViewById(R.id.tv_address);
        TextView tvTotalAmount = d.findViewById(R.id.tv_total_amount);
        TextView tvPaymentMethod = d.findViewById(R.id.tv_payment_method);
        TextView tvOrderStatus = d.findViewById(R.id.tv_order_status);
        Button btnMarkDelivered = d.findViewById(R.id.btn_mark_delivered);
        ImageView close = d.findViewById(R.id.btn_close);

        tvOrderId.setText(String.valueOf(order.getOrderId()));
        tvCustomerName.setText(order.getCustomerName());
        tvContactNo.setText(order.getContactNumber());
        tvAddress.setText(order.getAddress());
        tvTotalAmount.setText(String.format("%.2f", order.getTotalAmount()));
        tvPaymentMethod.setText(order.getPaymentMethod());
        tvOrderStatus.setText(order.getStatus());

        btnMarkDelivered.setOnClickListener(v -> {
            markAsDelivered(order);
            d.dismiss();
        });

        close.setOnClickListener(v -> d.dismiss());
        d.show();
    }

    // Automatic refresh timer -----
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            fetchOrders();
            handler.postDelayed(this, REFRESH_INTERVAL);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRunnable);
    }

    public void updateRiderName(String fullName) {
        if (tvRiderName != null) {
            tvRiderName.setText(fullName);
        }
    }
}
