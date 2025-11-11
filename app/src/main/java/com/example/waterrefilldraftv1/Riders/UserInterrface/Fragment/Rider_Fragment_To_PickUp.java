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
import com.example.waterrefilldraftv1.Riders.Utils.DateTimeUtils;
import com.example.waterrefilldraftv1.Riders.models.PickupOrder;
import com.example.waterrefilldraftv1.Riders.models.Rider;
import com.example.waterrefilldraftv1.Riders.models.RiderOrdersResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

                int todayCount = 0;
                int tomorrowCount = 0;

                if (allOrders != null) {
                    for (PickupOrder order : allOrders) {
                        // ✅ FIXED: Only add TODAY's pickup orders
                        if ("to_pickup".equalsIgnoreCase(order.getStatus()) &&
                                order.getCustomerName() != null &&
                                order.getItems() != null &&
                                !order.getItems().isEmpty() &&
                                order.shouldShowInPickupList()) {

                            pickupList.add(order);
                            todayCount++;
                            Log.d("TO_PICKUP_FILTER", "✅ TODAY Pickup: " + order.getOrderId() + " - " + order.getPickupDatetime());
                        } else if ("to_pickup".equalsIgnoreCase(order.getStatus()) && order.isTomorrowOrder()) {
                            tomorrowCount++;
                            Log.d("TO_PICKUP_FILTER", "🚫 TOMORROW Pickup (HIDDEN): " + order.getOrderId() + " - " + order.getPickupDatetime());
                        }
                    }
                }

                // ✅ NEW: SORT PICKUP ORDERS BY TIME (ASCENDING - Earliest First)
                Collections.sort(pickupList, (o1, o2) -> {
                    Date date1 = DateTimeUtils.parseDateTimeForSorting(o1.getPickupDatetime());
                    Date date2 = DateTimeUtils.parseDateTimeForSorting(o2.getPickupDatetime());

                    if (date1 == null && date2 == null) return 0;
                    if (date1 == null) return -1;
                    if (date2 == null) return 1;

                    // Normal comparison for ascending order (earliest first)
                    return date1.compareTo(date2);
                });

                adapter.notifyDataSetChanged();
                updatePendingCount();
                showEmpty(pickupList.isEmpty());

                // Log sorted order for verification
                for (PickupOrder order : pickupList) {
                    Log.d("TO_PICKUP_SORTED", "Sorted: " + order.getFormattedPickupDatetime());
                }

                Log.d("TO_PICKUP_FRAGMENT", "Results - Today: " + todayCount + ", Tomorrow: " + tomorrowCount + ", Total: " + pickupList.size());
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

    // ✅ FIXED: Update the method call in markAsPickedUp
    private void markAsPickedUp(PickupOrder order) {
        // ✅ DOUBLE CHECK: Prevent marking tomorrow's orders
        if (!order.canMarkAsPickedUp()) {
            if (order.isTomorrowOrder()) {
                Toast.makeText(requireContext(), "❌ Cannot mark tomorrow's order as picked up", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(requireContext(), "❌ This order cannot be picked up at this time", Toast.LENGTH_LONG).show();
            }
            return;
        }

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
                            Toast.makeText(requireContext(), "✅ Marked as Picked Up", Toast.LENGTH_SHORT).show();
                            fetchPickups(); // ✅ FIXED: Call the correct method name
                        } else {
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

    private void showPickupDetailsDialog(Context ctx, PickupOrder order) {
        Dialog d = new Dialog(ctx);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.rider_dialog_pickup_details);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.getWindow().setLayout((int)(getResources().getDisplayMetrics().widthPixels * 0.9),
                LayoutParams.WRAP_CONTENT);

        // ✅ FIXED: Get ALL views including the new ones
        TextView tvOrderId = d.findViewById(R.id.tv_order_id);
        ImageView iv = d.findViewById(R.id.iv_gallon_image);
        TextView tvCustomer = d.findViewById(R.id.tv_customer_name);
        TextView tvContactNo = d.findViewById(R.id.tv_contact_no); // ✅ ADD THIS
        TextView tvAddress = d.findViewById(R.id.tv_address); // ✅ ADD THIS
        TextView tvQty = d.findViewById(R.id.tv_gallon_quantity);
        TextView tvGallonName = d.findViewById(R.id.tv_gallon_name); // ✅ ADD THIS
        TextView tvMulti = d.findViewById(R.id.tv_more_items);
        TextView tvPickupTime = d.findViewById(R.id.tv_pickup_time); // ✅ THIS EXISTS IN YOUR LAYOUT
        TextView tvPaymentMethod = d.findViewById(R.id.tv_payment_method); // ✅ ADD THIS
        TextView tvTotalAmount = d.findViewById(R.id.tv_total_amount); // ✅ ADD THIS
        TextView tvOrderStatus = d.findViewById(R.id.tv_order_status); // ✅ ADD THIS
        Button btn = d.findViewById(R.id.btn_mark_picked_up);
        ImageView close = d.findViewById(R.id.btn_close);

        // ✅ SET ALL ORDER DETAILS
        tvOrderId.setText(order.getOrderId());
        tvCustomer.setText(order.getCustomerName());
        tvContactNo.setText(order.getContactNumber() != null ? order.getContactNumber() : "N/A");
        tvAddress.setText(order.getAddress());
        tvQty.setText("x" + order.getPrimaryQuantity());
        tvGallonName.setText(order.getPrimaryGallonName());
        tvPaymentMethod.setText(order.getPaymentMethod() != null ? order.getPaymentMethod() : "Cash");
        tvTotalAmount.setText(order.getFormattedTotal());
        tvOrderStatus.setText(order.getDisplayStatus());

        // ✅ FIXED: Set formatted pickup time instead of raw datetime
        String pickupInfo = order.getFormattedPickupDatetime();
        String urgentTime = order.getUrgentRelativeTime();

        if (!urgentTime.isEmpty()) {
            tvPickupTime.setText(pickupInfo + " • " + urgentTime);
        } else {
            tvPickupTime.setText(pickupInfo); // This will show "Today, 1:00 PM" etc.
        }

        if (order.hasMultipleGallons()) {
            tvMulti.setText("+" + (order.getItemCount() - 1) + " more items");
            tvMulti.setVisibility(View.VISIBLE);
        } else {
            tvMulti.setVisibility(View.GONE);
        }

        Glide.with(ctx)
                .load(order.getPrimaryImageUrl())
                .placeholder(R.drawable.img_slim_container)
                .into(iv);

        // ✅ FIXED: Update button logic for late pickups
        boolean canMarkAsPickedUp = order.canMarkAsPickedUp();
        btn.setEnabled(canMarkAsPickedUp);
        btn.setAlpha(canMarkAsPickedUp ? 1.0f : 0.5f);

        if (!canMarkAsPickedUp) {
            if (order.isTomorrowOrder()) {
                btn.setText("Tomorrow's Order");
            } else {
                btn.setText("Not Available");
            }
        } else {
            // Show "Late Pickup" for today's orders that are past their scheduled time
            if (DateTimeUtils.isPickupInPast(order.getPickupDatetime()) &&
                    DateTimeUtils.isPickupScheduledForToday(order.getPickupDatetime())) {
                btn.setText("Late Pickup");
            } else {
                btn.setText("Mark as Picked Up");
            }
        }

        btn.setOnClickListener(v -> {
            if (order.canMarkAsPickedUp()) {
                markAsPickedUp(order);
                d.dismiss();
            } else {
                String message = order.isTomorrowOrder() ?
                        "This order is scheduled for tomorrow and cannot be marked as picked up yet." :
                        "This order cannot be marked as picked up at this time.";
                Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
            }
        });

        close.setOnClickListener(v -> d.dismiss());
        d.show();
    }
}
