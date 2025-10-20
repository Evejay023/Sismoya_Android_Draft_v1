package com.example.waterrefilldraftv1.Customer.UserInterface.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Customer.network.ApiService;
import com.example.waterrefilldraftv1.Customer.network.RetrofitClient;
import com.example.waterrefilldraftv1.Customer.models.OrderOverview;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_fragment_orders, container, false);

        LinearLayout list = view.findViewById(R.id.orders_list_container);
        TextView empty = view.findViewById(R.id.tv_orders_placeholder);

        fetchOrders(view.getContext(), list, empty);

        return view;
    }

    private void fetchOrders(Context ctx, LinearLayout list, TextView empty) {
        SharedPreferences sp = ctx.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String token = sp.getString("token", null);
        if (token == null) return;
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getLatestOrders("Bearer " + token).enqueue(new Callback<com.example.waterrefilldraftv1.Customer.models.ApiResponse>() {
            @Override public void onResponse(Call<com.example.waterrefilldraftv1.Customer.models.ApiResponse> call, Response<com.example.waterrefilldraftv1.Customer.models.ApiResponse> res) {
                if (res.isSuccessful() && res.body()!=null && !res.body().isError() && !res.body().getLatestOrders().isEmpty()) {
                    empty.setVisibility(View.GONE);
                    list.removeAllViews();
                    for (OrderOverview o : res.body().getLatestOrders()) {
                        TextView tv = new TextView(ctx);
                        tv.setText("#" + o.orderId + " • " + o.status + " • ₱" + String.format("%.2f", o.totalAmount));
                        tv.setTextColor(0xFFFFFFFF);
                        tv.setTextSize(14);
                        list.addView(tv);
                    }
                } else {
                    empty.setText("No orders yet. Tap to shop.");
                    empty.setOnClickListener(v -> {
                        // no-op: user can navigate to containers tab via bottom nav
                    });
                }
            }
            @Override public void onFailure(Call<com.example.waterrefilldraftv1.Customer.models.ApiResponse> call, Throwable t) {
                empty.setText("Failed to load orders. Try again later.");
            }
        });
    }
}