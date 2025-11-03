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
    import com.example.waterrefilldraftv1.Riders.Adapter.RiderDeliveryAdapter;
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

                    deliveryList.clear();

                    List<PickupOrder> all = response.body().getData();

                    if (all != null) {
                        for (PickupOrder o : all) {
                            if ("to_deliver".equalsIgnoreCase(o.getStatus())) {

                                deliveryList.add(new RiderDelivery(
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
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<RiderOrdersResponse> call, Throwable t) {
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
                            if (response.isSuccessful()) {
                                Toast.makeText(requireContext(), "Delivered ✅", Toast.LENGTH_SHORT).show();
                                fetchDeliveries();
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


        private void showDeliverDetailsDialog(Context ctx, RiderDelivery order) {
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
    }
