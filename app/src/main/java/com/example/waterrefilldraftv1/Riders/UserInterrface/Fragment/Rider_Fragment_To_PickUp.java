package com.example.waterrefilldraftv1.Riders.UserInterrface.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.example.waterrefilldraftv1.Riders.models.PickupOrder;
import com.example.waterrefilldraftv1.Riders.models.Rider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Rider_Fragment_To_PickUp extends Fragment {

    private static final String ARG_RIDER = "rider_data";
    private RecyclerView rvPickups;
    private PickupAdapter adapter;
    private List<PickupOrder> pickupList = new ArrayList<>();
    private ApiService apiService;
    private Rider currentRider;

    public static Rider_Fragment_To_PickUp newInstance(Rider rider) {
        Rider_Fragment_To_PickUp f = new Rider_Fragment_To_PickUp();
        Bundle b = new Bundle();
        b.putSerializable(ARG_RIDER, rider);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentRider = (Rider) getArguments().getSerializable(ARG_RIDER);
        }
        apiService = RetrofitClient.getApiService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.rider_fragment_to_pickup, container, false);
        rvPickups = view.findViewById(R.id.rv_pickup_orders);
        rvPickups.setLayoutManager(new LinearLayoutManager(requireContext()));

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
        if (token == null) return;

        apiService.getRiderOrders("Bearer " + token).enqueue(new Callback<List<PickupOrder>>() {
            @Override
            public void onResponse(Call<List<PickupOrder>> call, Response<List<PickupOrder>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    pickupList.clear();

                    for (PickupOrder o : response.body()) {
                        String s = o.getStatus().toLowerCase();

                        if (s.equals("to_pickup")) {
                            pickupList.add(o);
                        }
                    }

                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(requireContext(), "Failed to load pick ups", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PickupOrder>> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void markAsPickedUp(PickupOrder order) {
        String token = TokenManager.getToken(requireContext());
        if (token == null) return;

        Map<String, String> body = new HashMap<>();
        body.put("newStatus", "picked_up");

        apiService.updateRiderOrderStatus("Bearer " + token, order.getOrderId(), body)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), "Marked as picked up → to deliver", Toast.LENGTH_SHORT).show();
                            fetchPickups(); // refresh pickup list
                        } else {
                            Toast.makeText(requireContext(), "Failed to update status", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showPickupDetailsDialog(Context ctx, PickupOrder order) {
        Dialog d = new Dialog(ctx);
        d.setContentView(R.layout.rider_dialog_delivery_details); // use the shared dialog layout
        if (d.getWindow() != null) d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvOrderId = d.findViewById(R.id.tv_order_id);
        TextView tvCustomerName = d.findViewById(R.id.tv_customer_name);
        TextView tvContactNo = d.findViewById(R.id.tv_contact_no);
        TextView tvAddress = d.findViewById(R.id.tv_address);
        TextView tvTotalAmount = d.findViewById(R.id.tv_total_amount);
        TextView tvPaymentMethod = d.findViewById(R.id.tv_payment_method);
        TextView tvOrderStatus = d.findViewById(R.id.tv_order_status);
        Button btnMark = d.findViewById(R.id.btn_mark_delivered); // reused button, change label
        ImageView close = d.findViewById(R.id.btn_close);

        tvOrderId.setText(String.valueOf(order.getOrderId()));
        tvCustomerName.setText(order.getCustomerName());
        tvContactNo.setText(order.getContactNumber());
        tvAddress.setText(order.getAddress());
        tvTotalAmount.setText(String.format("%.2f", order.getTotalAmount()));
        tvPaymentMethod.setText(order.getPaymentMethod());
        tvOrderStatus.setText(order.getStatus());

        // For pickup dialog the button must mark as picked up:
        btnMark.setText("Mark as Picked Up");
        btnMark.setOnClickListener(v -> {
            markAsPickedUp(order);
            d.dismiss();
        });

        close.setOnClickListener(v -> d.dismiss());
        d.show();
    }
}
