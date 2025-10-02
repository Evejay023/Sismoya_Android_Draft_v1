package com.example.waterrefilldraftv1.ui.s.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.models.User;
import com.google.gson.Gson;

public class DashboardFragment extends Fragment {

    private static final String ARG_USER = "arg_user";
    private User currentUser;

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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        if (getArguments() != null) {
            String userJson = getArguments().getString(ARG_USER);
            currentUser = new Gson().fromJson(userJson, User.class);
        }

        // FIX: use tv_customer_name instead of tv_welcome
        TextView tvCustomerName = view.findViewById(R.id.tv_customer_name);
        if (tvCustomerName != null && currentUser != null) {
            String fullName = currentUser.getFirstName() + " " + currentUser.getLastName();
            tvCustomerName.setText(fullName);
        }

        return view;
    }
}
