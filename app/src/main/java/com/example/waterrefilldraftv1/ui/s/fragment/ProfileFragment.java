package com.example.waterrefilldraftv1.ui.s.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.models.User;
import com.example.waterrefilldraftv1.ui.s.activities.LoginActivity;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserContact, tvUserEmail;
    private LinearLayout llOrders, llAddress, llChangePassword, llLogout;
    private User currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Init views
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserContact = view.findViewById(R.id.tv_user_contact);
        tvUserEmail = view.findViewById(R.id.tv_user_email);

        llOrders = view.findViewById(R.id.ll_orders);
        llAddress = view.findViewById(R.id.ll_address);
        llChangePassword = view.findViewById(R.id.ll_change_password);
        llLogout = view.findViewById(R.id.ll_logout);

        // Load user data from arguments
        if (getArguments() != null) {
            String name = getArguments().getString("user_name", "Customer Name");
            String contact = getArguments().getString("user_contact", "09XXXXXXXXX");
            String email = getArguments().getString("user_email", "customer@email.com");

            tvUserName.setText(name);
            tvUserContact.setText(contact);
            tvUserEmail.setText(email);
        }

        // Handle clicks
        setupClickListeners();

        return view;
    }

    private void setupClickListeners() {
        llOrders.setOnClickListener(v -> {
            // TODO: Navigate to OrdersFragment or OrdersActivity
        });

        llAddress.setOnClickListener(v -> {
            // TODO: Navigate to Address screen
        });

        llChangePassword.setOnClickListener(v -> {
            // TODO: Open Change Password Dialog/Activity
        });

        llLogout.setOnClickListener(v -> {
            // Example: clear session and go back to LoginActivity
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
