package com.example.waterrefilldraftv1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

/**
 * DashboardFragment - Home screen showing user stats
 * Displays current orders, pending orders, total orders
 */
public class DashboardFragment extends Fragment {

    private TextView tvWelcome, tvCurrentOrders, tvPendingOrders, tvTotalOrders;
    private CardView cardOrderHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initViews(view);
        setupUserInfo();
        setupOrderStats();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvCurrentOrders = view.findViewById(R.id.tv_current_orders);
        tvPendingOrders = view.findViewById(R.id.tv_pending_orders);
        tvTotalOrders = view.findViewById(R.id.tv_total_orders);
        cardOrderHistory = view.findViewById(R.id.card_order_history);
    }

    private void setupUserInfo() {
        // Get user data from arguments
        Bundle args = getArguments();
        if (args != null) {
            String userName = args.getString("user_name", "Customer");
            tvWelcome.setText("Welcome,\n" + userName);
        }
    }

    private void setupOrderStats() {
        // For now, use sample data
        // In real app, fetch from database/API
        tvCurrentOrders.setText("4");
        tvPendingOrders.setText("5");
        tvTotalOrders.setText("4");
    }

    private void setupClickListeners() {
        cardOrderHistory.setOnClickListener(v -> {
            // Navigate to order history
            // For now, just show toast
            if (getContext() != null) {
                android.widget.Toast.makeText(getContext(), "Order History clicked", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}