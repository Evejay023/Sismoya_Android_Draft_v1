package com.example.waterrefilldraftv1.Customer.UserInterface.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.waterrefilldraftv1.Customer.models.OrderOverview;
import com.example.waterrefilldraftv1.Customer.models.OrderStats;
import com.example.waterrefilldraftv1.Customer.models.User;
import com.example.waterrefilldraftv1.Global.network.ApiService;
import com.example.waterrefilldraftv1.Global.network.RetrofitClient;
import com.example.waterrefilldraftv1.Global.network.TokenManager;
import com.example.waterrefilldraftv1.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";

    private TextView tvCustomerName;
    private TextView tvPendingOrders, tvCompletedOrders, tvCancelledOrders, tvTotalOrders;
    private TextView tvOrderTitle, tvOrderStatus, tvOrderDate;
    private ImageView ivOrderImage;

    private ApiService apiService;

    public static DashboardFragment newInstance(User user) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        if (user != null) {
            args.putString("user_name", user.getFullName());
            args.putString("user_email", user.getEmail());
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_fragment_dashboard, container, false);

        // Initialize views
        tvCustomerName = view.findViewById(R.id.tv_customer_name);
        tvPendingOrders = view.findViewById(R.id.tv_pending_orders);
        tvCompletedOrders = view.findViewById(R.id.tv_completed_orders);
        tvCancelledOrders = view.findViewById(R.id.tv_cancelled_orders);
        tvTotalOrders = view.findViewById(R.id.tv_total_orders);
        tvOrderTitle = view.findViewById(R.id.tv_order_title);
        tvOrderStatus = view.findViewById(R.id.tv_order_status);
        tvOrderDate = view.findViewById(R.id.tv_order_date);
        ivOrderImage = view.findViewById(R.id.iv_order_image);

        apiService = RetrofitClient.getApiService();

        fetchCustomerName();
        fetchOrderStats();
        fetchLatestOrders();

        return view;
    }

    // --------------------------------------------------------------------
    // Fetch customer display name
    // --------------------------------------------------------------------
    private void fetchCustomerName() {
        Bundle args = getArguments();
        if (args != null && args.getString("user_name") != null) {
            tvCustomerName.setText(args.getString("user_name"));
        } else {
            tvCustomerName.setText("Customer");
        }
    }

    // --------------------------------------------------------------------
    // Fetch order statistics
    // --------------------------------------------------------------------
    private void fetchOrderStats() {
        String token = TokenManager.getToken(requireContext());
        if (token == null) {
            Toast.makeText(requireContext(), "Missing token. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Pass dummy "TRY" parameter so backend validation succeeds
        apiService.getOrderStats("Bearer " + token, "TRY").enqueue(new Callback<OrderStats>() {
            @Override
            public void onResponse(@NonNull Call<OrderStats> call, @NonNull Response<OrderStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderStats stats = response.body();
                    tvPendingOrders.setText(String.valueOf(stats.getPending()));
                    tvCompletedOrders.setText(String.valueOf(stats.getCompleted()));
                    tvCancelledOrders.setText(String.valueOf(stats.getCancelled()));
                    tvTotalOrders.setText(String.valueOf(stats.getTotal()));
                    Log.d(TAG, "✅ Order Stats Loaded Successfully");
                } else {
                    Log.w(TAG, "⚠️ Failed to fetch order stats: " + response.message());
                    Toast.makeText(requireContext(), "Failed to load order stats.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderStats> call, @NonNull Throwable t) {
                Log.e(TAG, "❌ Error loading stats: " + t.getMessage(), t);
                Toast.makeText(requireContext(), "Network error while loading stats.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --------------------------------------------------------------------
    // Fetch latest orders
    // --------------------------------------------------------------------
    private void fetchLatestOrders() {
        String token = TokenManager.getToken(requireContext());
        if (token == null) {
            Toast.makeText(requireContext(), "Missing token. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Pass dummy "TRY" parameter so backend validation succeeds
        apiService.getLatestOrders("Bearer " + token, "TRY").enqueue(new Callback<List<OrderOverview>>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderOverview>> call,
                                   @NonNull Response<List<OrderOverview>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    OrderOverview latest = response.body().get(0);
                    tvOrderTitle.setText("Order #" + latest.getOrderId());
                    tvOrderStatus.setText("Status: " + latest.getStatus());

                    try {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                        String formattedDate = outputFormat.format(inputFormat.parse(latest.getCreatedAt()));
                        tvOrderDate.setText(formattedDate);
                    } catch (Exception e) {
                        tvOrderDate.setText(latest.getCreatedAt());
                    }

                    ivOrderImage.setImageResource(R.drawable.img_round_container);
                    Log.d(TAG, "✅ Latest Order Loaded Successfully");
                } else {
                    Log.w(TAG, "⚠️ No recent orders found or failed: " + response.message());
                    tvOrderTitle.setText("No Recent Orders");
                    tvOrderStatus.setText("");
                    tvOrderDate.setText("");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OrderOverview>> call, @NonNull Throwable t) {
                Log.e(TAG, "❌ Error loading latest orders: " + t.getMessage(), t);
                Toast.makeText(requireContext(), "Network error while loading latest orders.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
