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
import com.example.waterrefilldraftv1.Riders.Adapter.RiderDeliveryAdapter;
import com.example.waterrefilldraftv1.Riders.Utils.ImageFormatter;
import com.example.waterrefilldraftv1.Riders.Utils.StatusFormatter;
import com.example.waterrefilldraftv1.Riders.models.PickupOrder;
import com.example.waterrefilldraftv1.Riders.models.RiderDelivery;
import com.example.waterrefilldraftv1.Riders.models.Rider;
import com.example.waterrefilldraftv1.Riders.models.RiderOrdersResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Rider_Fragment_To_Deliver extends Fragment {

    private static final String ARG_RIDER = "rider_data";
    private RecyclerView rvDeliveryOrders;
    private RiderDeliveryAdapter adapter;
    private List<RiderDelivery> deliveryList = new ArrayList<>();
    private ApiService apiService;
    private Rider currentRider;

    // UI Elements for pending count
    private TextView tvPendingDeliveries;

    public static Rider_Fragment_To_Deliver newInstance(Rider rider) {
        Rider_Fragment_To_Deliver f = new Rider_Fragment_To_Deliver();
        Bundle b = new Bundle();
        b.putSerializable(ARG_RIDER, rider);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) currentRider = (Rider) getArguments().getSerializable(ARG_RIDER);
        apiService = RetrofitClient.getApiService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rider_fragment_to_deliver, container, false);

        rvDeliveryOrders = view.findViewById(R.id.rv_delivery_orders);
        rvDeliveryOrders.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize the pending deliveries text view
        tvPendingDeliveries = view.findViewById(R.id.tv_pending_deliveries);

        adapter = new RiderDeliveryAdapter(deliveryList, new RiderDeliveryAdapter.OnDeliverActionListener() {
            @Override
            public void onViewDetails(RiderDelivery order) {
                showDeliverDetailsDialog(requireContext(), order);
            }

            @Override
            public void onMarkDelivered(RiderDelivery order) {
                markAsDelivered(order);
            }
        });

        rvDeliveryOrders.setAdapter(adapter);
        fetchDeliveries();

        return view;
    }

    private void fetchDeliveries() {
        String token = TokenManager.getToken(requireContext());
        if (token == null) return;

        apiService.getRiderOrders("Bearer " + token).enqueue(new Callback<RiderOrdersResponse>() {
            @Override
            public void onResponse(Call<RiderOrdersResponse> call, Response<RiderOrdersResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Failed to load deliveries", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!response.body().isSuccess()) {
                    Toast.makeText(requireContext(), "No delivery orders found", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<PickupOrder> allOrders = response.body().getData();
                deliveryList.clear();

                if (allOrders != null) {
                    for (PickupOrder o : allOrders) {
                        if ("to_deliver".equalsIgnoreCase(o.getStatus())) {
                            // Create RiderDelivery with all the new fields
                            deliveryList.add(new RiderDelivery(
                                    o.getOrderId(),
                                    o.getCustomerName(),
                                    o.getAddress(),
                                    o.getContactNumber(),
                                    o.getStatus(),
                                    o.getPaymentMethod(),
                                    o.getTotalPriceDouble(), // Use getTotalPriceDouble() instead of getTotalAmount()
                                    o.getPrimaryGallonName(), // Get from PickupOrder
                                    o.getPrimaryQuantity(),   // Get from PickupOrder
                                    o.getPrimaryImageUrl(),   // Get from PickupOrder
                                    o.getItemCount()          // Get from PickupOrder
                            ));
                            Log.d("DELIVERY_FILTER", "Added order to delivery list: " + o.getOrderId());
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                updatePendingCount();
                Log.d("DELIVERY_FRAGMENT", "Final delivery list size: " + deliveryList.size());
            }

            @Override
            public void onFailure(Call<RiderOrdersResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FETCH_DELIVERIES", "Error", t);
            }
        });
    }

    private void updatePendingCount() {
        if (tvPendingDeliveries != null) {
            tvPendingDeliveries.setText("Pending Deliveries: " + deliveryList.size());
        }
    }

    private void markAsDelivered(RiderDelivery order) {
        String token = TokenManager.getToken(requireContext());
        if (token == null) return;

        Map<String, String> body = new HashMap<>();
        body.put("newStatus", "delivered");

        // ✅ FIXED: order.getOrderId() returns String
        apiService.updateRiderOrderStatus("Bearer " + token, order.getOrderId(), body)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(requireContext(), "Marked as Delivered ✅", Toast.LENGTH_SHORT).show();
                            fetchDeliveries(); // Refresh the delivery list
                        } else {
                            Toast.makeText(requireContext(), "Failed to update status", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDeliverDetailsDialog(Context ctx, RiderDelivery order) {
        Dialog d = new Dialog(ctx);
        d.setContentView(R.layout.rider_dialog_delivery_details);
        if (d.getWindow() != null) d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Find all views
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
        tvOrderId.setText(String.valueOf(order.getOrderId()));
        tvCustomerName.setText(order.getCustomerName());
        tvContactNo.setText(order.getContactNumber());
        tvAddress.setText(order.getAddress());
        tvTotalAmount.setText(String.format("₱%.2f", order.getTotalAmount()));
        tvPaymentMethod.setText(order.getPaymentMethod());
        tvOrderStatus.setText(StatusFormatter.format(order.getStatus()));

        // Set gallon details using the new fields
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
}