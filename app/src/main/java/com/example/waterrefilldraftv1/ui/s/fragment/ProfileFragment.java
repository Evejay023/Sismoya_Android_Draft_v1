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

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail;
    private User currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the profile layout
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);

        // Get arguments from DashboardActivity
        Bundle bundle = getArguments();
        if (bundle != null) {
            String name = bundle.getString("user_name", "Customer Name");
            String email = bundle.getString("user_email", "customer@email.com");

            tvUserName.setText(name);
            tvUserEmail.setText(email);
        }

        return view;
    }
}
