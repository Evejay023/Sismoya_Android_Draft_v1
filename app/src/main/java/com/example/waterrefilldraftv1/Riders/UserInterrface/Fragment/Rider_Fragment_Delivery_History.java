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

import com.example.waterrefilldraftv1.Global.network.ApiResponse;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Global.network.RetrofitClient;
import com.example.waterrefilldraftv1.Riders.Adapter.CompletedOrdersAdapter;
import com.example.waterrefilldraftv1.Riders.models.CompletedOrderModel;
import com.example.waterrefilldraftv1.Global.network.ApiService;
import com.example.waterrefilldraftv1.Riders.models.Rider;
import com.example.waterrefilldraftv1.Global.network.TokenManager;

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
        String token = TokenManager.getToken(requireContext());
        if (token == null) {
            Toast.makeText(getContext(), "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ FIX: Use the direct array call instead of wrapper
        apiService.getDeliveryHistory("Bearer " + token).enqueue(new Callback<List<CompletedOrderModel>>() {
            @Override
            public void onResponse(Call<List<CompletedOrderModel>> call, Response<List<CompletedOrderModel>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<CompletedOrderModel> orders = response.body();

                    if (orders != null && !orders.isEmpty()) {
                        adapter.updateOrders(orders);
                        Log.d("DELIVERY_HISTORY", "Loaded " + orders.size() + " completed orders");
                    } else {
                        Toast.makeText(getContext(), "No delivery history found", Toast.LENGTH_SHORT).show();
                        adapter.updateOrders(new ArrayList<>());
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch history: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("DELIVERY_HISTORY", "Response not successful: " + response.code());

                    // ✅ FALLBACK: Try the wrapper method if direct array fails
                    tryWrapperMethod(token);
                }
            }

            @Override
            public void onFailure(Call<List<CompletedOrderModel>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DELIVERY_HISTORY", "Error loading orders", t);

                // ✅ FALLBACK: Try the wrapper method if direct call fails
                tryWrapperMethod(token);
            }
        });
    }

    private void tryWrapperMethod(String token) {
        // Fallback to wrapper method
        apiService.getDeliveryHistoryWithWrapper("Bearer " + token).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        List<CompletedOrderModel> orders = apiResponse.getDataAsList(CompletedOrderModel.class);

                        if (orders != null && !orders.isEmpty()) {
                            adapter.updateOrders(orders);
                            Log.d("DELIVERY_HISTORY", "Loaded " + orders.size() + " completed orders (wrapper method)");
                        } else {
                            Toast.makeText(getContext(), "No delivery history found", Toast.LENGTH_SHORT).show();
                            adapter.updateOrders(new ArrayList<>());
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch history: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("DELIVERY_HISTORY", "Wrapper method also failed", t);
            }
        });
    }
}