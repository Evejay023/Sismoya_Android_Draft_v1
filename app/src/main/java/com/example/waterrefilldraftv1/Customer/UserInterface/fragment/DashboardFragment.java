package com.example.waterrefilldraftv1.Customer.UserInterface.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Customer.models.User;
import com.example.waterrefilldraftv1.Customer.models.OrderStats;
import com.example.waterrefilldraftv1.Customer.network.ApiService;
import com.example.waterrefilldraftv1.Customer.network.RetrofitClient;
import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private static final String ARG_USER = "arg_user";
    private User currentUser;
    private TextView tvPending, tvCompleted, tvCancelled, tvTotal;

    public static DashboardFragment newInstance(User user) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, new Gson().toJson(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_fragment_dashboard, container, false);

        if (getArguments() != null) {
            String userJson = getArguments().getString(ARG_USER);
            currentUser = new Gson().fromJson(userJson, User.class);
        }

        // FIX: use tv_customer_name instead of tv_welcome
        TextView tvCustomerName = view.findViewById(R.id.tv_customer_name);
        tvPending = view.findViewById(R.id.tv_pending_orders);
        tvCompleted = view.findViewById(R.id.tv_completed_orders);
        tvCancelled = view.findViewById(R.id.tv_cancelled_orders);
        tvTotal = view.findViewById(R.id.tv_total_orders);

        if (tvCustomerName != null && currentUser != null) {
            String fullName = currentUser.getFirstName() + " " + currentUser.getLastName();
            tvCustomerName.setText(fullName);
        }

        fetchStatsAndLatest(view.getContext());

        return view;
    }

    private void fetchStatsAndLatest(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String token = sp.getString("token", null);
        if (token == null) return;
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);

        api.getOrderStats("Bearer " + token).enqueue(new Callback<com.example.waterrefilldraftv1.Customer.models.ApiResponse>() {
            @Override public void onResponse(Call<com.example.waterrefilldraftv1.Customer.models.ApiResponse> call, Response<com.example.waterrefilldraftv1.Customer.models.ApiResponse> res) {
                if (res.isSuccessful() && res.body()!=null && !res.body().isError()) {
                    OrderStats s = res.body().getOrderStats();
                    if (tvPending!=null) tvPending.setText(String.valueOf(s.pending));
                    if (tvCompleted!=null) tvCompleted.setText(String.valueOf(s.completed));
                    if (tvCancelled!=null) tvCancelled.setText(String.valueOf(s.cancelled));
                    if (tvTotal!=null) tvTotal.setText(String.valueOf(s.total));
                }
            }
            @Override public void onFailure(Call<com.example.waterrefilldraftv1.Customer.models.ApiResponse> call, Throwable t) { }
        });

        api.getLatestOrders("Bearer " + token).enqueue(new Callback<com.example.waterrefilldraftv1.Customer.models.ApiResponse>() {
            @Override public void onResponse(Call<com.example.waterrefilldraftv1.Customer.models.ApiResponse> call, Response<com.example.waterrefilldraftv1.Customer.models.ApiResponse> res) {
                // optional: could display preview items
            }
            @Override public void onFailure(Call<com.example.waterrefilldraftv1.Customer.models.ApiResponse> call, Throwable t) { }
        });
    }
}
