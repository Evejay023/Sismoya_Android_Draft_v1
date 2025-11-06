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
import android.view.Window;
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
import com.example.waterrefilldraftv1.Riders.Utils.DateTimeUtils;
import com.example.waterrefilldraftv1.Riders.Utils.ImageFormatter;
import com.example.waterrefilldraftv1.Riders.Utils.StatusFormatter;
import com.example.waterrefilldraftv1.Riders.models.PickupOrder;
import com.example.waterrefilldraftv1.Riders.models.Rider;
import com.example.waterrefilldraftv1.Riders.models.RiderDelivery;
import com.example.waterrefilldraftv1.Riders.models.RiderOrdersResponse;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Rider_Fragment_Dashboard extends Fragment {

    private static final String ARG_RIDER = "arg_rider";
    private Rider rider;

    private TextView tvRiderName, tvEmptyPickup, tvEmptyDelivery;
    private TextView tvDate, tvTime; // ADD THESE FOR DATE/TIME
    private RecyclerView rvDashboardPickups, rvDashboardDeliveries;

    private PickupAdapter pickupAdapter;
    private RiderDeliveryAdapter deliveryAdapter;

    private ApiService apiService;

    private List<PickupOrder> allPickup = new ArrayList<>();
    private List<RiderDelivery> allDelivery = new ArrayList<>();

    private Handler handler = new Handler();
    private final int REFRESH_INTERVAL = 5000; // 5 seconds for orders
    private final int TIME_UPDATE_INTERVAL = 30000; // 30 seconds for time updates

    // ADD: Runnable for updating time
    private final Runnable timeUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateCurrentDateTime();
            handler.postDelayed(this, TIME_UPDATE_INTERVAL);
        }
    };

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

        // INITIALIZE ALL VIEWS
        tvRiderName = view.findViewById(R.id.tv_rider_name);
        tvEmptyPickup = view.findViewById(R.id.tv_empty_pickup);
        tvEmptyDelivery = view.findViewById(R.id.tv_empty_delivery);

        // ADD: Initialize date and time views from your XML
        tvDate = view.findViewById(R.id.tv_date);
        tvTime = view.findViewById(R.id.tv_time);

        rvDashboardPickups = view.findViewById(R.id.rv_dashboard_pickups);
        rvDashboardDeliveries = view.findViewById(R.id.rv_dashboard_deliveries);

        rvDashboardPickups.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDashboardDeliveries.setLayoutManager(new LinearLayoutManager(getContext()));

        if (rider != null) {
            tvRiderName.setText(rider.getFullName());
        }

        // UPDATE: Set current date and time immediately
        updateCurrentDateTime();

        fetchOrders();
    }

    // ADD: Method to update date and time based on device time
    private void updateCurrentDateTime() {
        if (!isAdded()) return;

        try {
            // Get current device time
            Calendar calendar = Calendar.getInstance();

            // Format date: "September 15, 2025"
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            String currentDate = dateFormat.format(calendar.getTime());

            // Format time: "11:00 AM"
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String currentTime = timeFormat.format(calendar.getTime());

            // Update the TextViews
            if (tvDate != null) {
                tvDate.setText(currentDate);
            }
            if (tvTime != null) {
                tvTime.setText(currentTime);
            }

            Log.d("DASHBOARD_TIME", "Updated time: " + currentDate + " " + currentTime);

        } catch (Exception e) {
            Log.e("DASHBOARD_TIME", "Error updating time", e);
        }
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
                            updateEmptyState();
                            return;
                        }

                        RiderOrdersResponse apiResponse = response.body();

                        if (!apiResponse.isSuccess()) {
                            Log.d("DASHBOARD", "No orders found: Success flag is false");
                            allPickup.clear();
                            allDelivery.clear();
                            bindAdapters();
                            updateEmptyState();
                            return;
                        }

                        List<PickupOrder> all = apiResponse.getData();

                        if (all == null || all.isEmpty()) {
                            Log.d("DASHBOARD", "No orders data received or empty list");
                            allPickup.clear();
                            allDelivery.clear();
                            bindAdapters();
                            updateEmptyState();
                            return;
                        }

                        allPickup.clear();
                        allDelivery.clear();

                        int invalidCount = 0;
                        int todayPickupCount = 0;
                        int tomorrowPickupCount = 0;
                        int futurePickupCount = 0;

                        for (PickupOrder o : all) {
                            if (o.getStatus() == null) {
                                invalidCount++;
                                continue;
                            }

                            String s = o.getStatus().toLowerCase();

                            boolean isValidOrder = o.getCustomerName() != null &&
                                    o.getItems() != null &&
                                    !o.getItems().isEmpty();

                            if (!isValidOrder) {
                                invalidCount++;
                                continue;
                            }

                            if (s.equals("to_pickup")) {
                                // ✅ FIXED: Only show TODAY's pickup orders
                                if (o.shouldShowInPickupList()) {
                                    allPickup.add(o);
                                    todayPickupCount++;
                                    Log.d("DASHBOARD_FILTER", "✅ TODAY Pickup: " + o.getOrderId() + " - " + o.getPickupDatetime());
                                } else if (o.isTomorrowOrder()) {
                                    tomorrowPickupCount++;
                                    Log.d("DASHBOARD_FILTER", "🚫 TOMORROW Pickup (HIDDEN): " + o.getOrderId() + " - " + o.getPickupDatetime());
                                } else {
                                    futurePickupCount++;
                                    Log.d("DASHBOARD_FILTER", "🚫 FUTURE Pickup (HIDDEN): " + o.getOrderId() + " - " + o.getPickupDatetime());
                                }
                            } else if (s.equals("to_deliver")) {
                                allDelivery.add(new RiderDelivery(
                                        o.getOrderId(),
                                        o.getCustomerName(),
                                        o.getAddress(),
                                        o.getContactNumber(),
                                        o.getStatus(),
                                        o.getPaymentMethod(),
                                        o.getTotalPriceDouble(),
                                        o.getItems()
                                ));
                            }
                        }

                        // ✅ NEW: SORT PICKUP ORDERS BY TIME (ASCENDING - Earliest First)
                        Collections.sort(allPickup, (o1, o2) -> {
                            Date date1 = DateTimeUtils.parseDateTimeForSorting(o1.getPickupDatetime());
                            Date date2 = DateTimeUtils.parseDateTimeForSorting(o2.getPickupDatetime());

                            if (date1 == null && date2 == null) return 0;
                            if (date1 == null) return -1;
                            if (date2 == null) return 1;

                            // Normal comparison for ascending order (earliest first)
                            return date1.compareTo(date2);
                        });

                        // ✅ OPTIONAL: Sort delivery orders too if needed
                        Collections.sort(allDelivery, (d1, d2) -> {
                            // Add similar logic for delivery orders if they have datetime
                            return 0; // Adjust as needed
                        });

                        // Log filtering results
                        Log.d("DASHBOARD_FILTER", "Results - Today: " + todayPickupCount +
                                ", Tomorrow: " + tomorrowPickupCount +
                                ", Future: " + futurePickupCount +
                                ", Invalid: " + invalidCount);

                        // Log sorted times for verification
                        for (PickupOrder order : allPickup) {
                            Log.d("DASHBOARD_SORTED", "Sorted Pickup: " + order.getFormattedPickupDatetime());
                        }

                        bindAdapters();
                        updateEmptyState();

                        Log.d("DASHBOARD", "Pickup orders: " + allPickup.size() + ", Delivery orders: " + allDelivery.size());
                    }

                    @Override
                    public void onFailure(Call<RiderOrdersResponse> call, Throwable t) {
                        Toast.makeText(requireContext(), "Network dashboard error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        allPickup.clear();
                        allDelivery.clear();
                        bindAdapters();
                        updateEmptyState();
                    }
                });
    }

    private void bindAdapters() {
        List<PickupOrder> limitPickup = new ArrayList<>();
        List<RiderDelivery> limitDelivery = new ArrayList<>();

        for (int i = 0; i < Math.min(3, allPickup.size()); i++) {
            limitPickup.add(allPickup.get(i));
        }
        for (int i = 0; i < Math.min(3, allDelivery.size()); i++) {
            limitDelivery.add(allDelivery.get(i));
        }

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

    // ✅ IMPROVED: Mark as Picked Up (same as pickup fragment)
    private void markAsPickedUp(PickupOrder order) {
        String token = TokenManager.getToken(requireContext());
        if (token == null) {
            Toast.makeText(requireContext(), "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("newStatus", "picked_up");

        Toast.makeText(requireContext(), "Updating...", Toast.LENGTH_SHORT).show();

        apiService.updateRiderOrderStatus("Bearer " + token, order.getOrderId(), body)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful()) {
                            // ✅ SUCCESS - Always refresh when we get 200 response
                            Toast.makeText(requireContext(), "✅ Marked as Picked Up", Toast.LENGTH_SHORT).show();
                            fetchOrders(); // This will reload fresh data from server
                        } else {
                            // Handle different error cases
                            if (response.code() == 401) {
                                Toast.makeText(requireContext(), "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 404) {
                                Toast.makeText(requireContext(), "Order not found", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show();
                            }
                            Log.e("MARK_PICKED_UP", "Failed with code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("MARK_PICKED_UP", "Network failure", t);
                    }
                });
    }

    // ✅ IMPROVED: Mark as Delivered
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
                            fetchOrders(); // Refresh the dashboard
                        } else {
                            if (response.code() == 401) {
                                Toast.makeText(requireContext(), "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 404) {
                                Toast.makeText(requireContext(), "Order not found", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Dialogs -----
    private void showPickupDetailsDialog(Context ctx, PickupOrder order) {
        Dialog d = new Dialog(ctx);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.rider_dialog_pickup_details);
        if (d.getWindow() != null) {
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            d.getWindow().setLayout(
                    (int)(getResources().getDisplayMetrics().widthPixels * 0.9),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        // Find all views from PICKUP dialog layout
        ImageView ivGallon = d.findViewById(R.id.iv_gallon_image);
        TextView tvGallonName = d.findViewById(R.id.tv_gallon_name);
        TextView tvGallonQuantity = d.findViewById(R.id.tv_gallon_quantity);
        TextView tvMoreItems = d.findViewById(R.id.tv_more_items);
        TextView tvOrderId = d.findViewById(R.id.tv_order_id);
        TextView tvCustomerName = d.findViewById(R.id.tv_customer_name);
        TextView tvContactNo = d.findViewById(R.id.tv_contact_no);
        TextView tvAddress = d.findViewById(R.id.tv_address);
        TextView tvPickupTime = d.findViewById(R.id.tv_pickup_time);
        TextView tvTotalAmount = d.findViewById(R.id.tv_total_amount);
        TextView tvPaymentMethod = d.findViewById(R.id.tv_payment_method);
        TextView tvOrderStatus = d.findViewById(R.id.tv_order_status);
        Button btnMarkPickedUp = d.findViewById(R.id.btn_mark_picked_up);
        ImageView close = d.findViewById(R.id.btn_close);

        // Set order details using PickupOrder
        tvOrderId.setText(order.getOrderId());
        tvCustomerName.setText(order.getCustomerName());
        tvContactNo.setText(order.getContactNumber());
        tvAddress.setText(order.getAddress());
        tvTotalAmount.setText(order.getFormattedTotal());
        tvPaymentMethod.setText(order.getPaymentMethod());
        tvOrderStatus.setText(StatusFormatter.format(order.getStatus()));

        // Set gallon details
        tvGallonName.setText(order.getPrimaryGallonName());
        tvGallonQuantity.setText("x" + order.getPrimaryQuantity());

        // Show "more items" if applicable
        if (order.hasMultipleGallons()) {
            tvMoreItems.setText("+" + (order.getItemCount() - 1) + " more items");
            tvMoreItems.setVisibility(View.VISIBLE);
        } else {
            tvMoreItems.setVisibility(View.GONE);
        }

        // Set pickup time
        if (tvPickupTime != null) {
            tvPickupTime.setText(order.getFormattedPickupDatetime());
        }

        // Load image
        ImageFormatter.safeLoadGallonImage(
                ivGallon,
                order.getPrimaryImageUrl(),
                order.getPrimaryGallonName()
        );

        btnMarkPickedUp.setText("Mark as Picked Up");
        btnMarkPickedUp.setOnClickListener(v -> {
            markAsPickedUp(order);
            d.dismiss();
        });

        close.setOnClickListener(v -> d.dismiss());
        d.show();
    }

    private void showDeliveryDetailsDialog(Context ctx, RiderDelivery order) {
        Dialog d = new Dialog(ctx);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.rider_dialog_delivery_details);
        if (d.getWindow() != null) {
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            d.getWindow().setLayout(
                    (int)(getResources().getDisplayMetrics().widthPixels * 0.9),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        // Find all views from delivery dialog
        ImageView ivGallon = d.findViewById(R.id.iv_gallon_image);
        TextView tvGallonName = d.findViewById(R.id.tv_gallon_name);
        TextView tvGallonQuantity = d.findViewById(R.id.tv_gallon_quantity);
        TextView tvMoreItems = d.findViewById(R.id.tv_more_items);
        TextView tvOrderId = d.findViewById(R.id.tv_order_id);
        TextView tvCustomerName = d.findViewById(R.id.tv_customer_name);
        TextView tvContactNo = d.findViewById(R.id.tv_contact_no);
        TextView tvAddress = d.findViewById(R.id.tv_address);
        TextView tvTotalAmount = d.findViewById(R.id.tv_total_amount);
        TextView tvPaymentMethod = d.findViewById(R.id.tv_payment_method);
        TextView tvOrderStatus = d.findViewById(R.id.tv_order_status);
        Button btnMarkDelivered = d.findViewById(R.id.btn_mark_delivered);
        ImageView close = d.findViewById(R.id.btn_close);

        // Set order details
        tvOrderId.setText(order.getOrderId());
        tvCustomerName.setText(order.getCustomerName());
        tvContactNo.setText(order.getContactNumber());
        tvAddress.setText(order.getAddress());
        tvTotalAmount.setText(String.format("₱%.2f", order.getTotalAmount()));
        tvPaymentMethod.setText(order.getPaymentMethod());
        tvOrderStatus.setText(StatusFormatter.format(order.getStatus()));

        tvGallonName.setText(order.getGallonName());
        tvGallonQuantity.setText("x" + order.getGallonQuantity());

        // Show "more items" if there are multiple items
        if (order.hasMultipleItems()) {
            tvMoreItems.setText("+" + (order.getItemCount() - 1) + " more items");
            tvMoreItems.setVisibility(View.VISIBLE);
        } else {
            tvMoreItems.setVisibility(View.GONE);
        }

        // Load image using ImageFormatter
        ImageFormatter.safeLoadGallonImage(
                ivGallon,
                order.getImageUrl(),
                order.getGallonName()
        );

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
        // Start both timers
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
        handler.postDelayed(timeUpdateRunnable, TIME_UPDATE_INTERVAL);

        // Also update time immediately when resuming
        updateCurrentDateTime();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Remove both timers
        handler.removeCallbacks(refreshRunnable);
        handler.removeCallbacks(timeUpdateRunnable);
    }

    public void updateRiderName(String fullName) {
        if (tvRiderName != null) {
            tvRiderName.setText(fullName);
        }
    }
}