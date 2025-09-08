package com.example.waterrefilldraftv1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail;
    private Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        setupUserInfo();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void setupUserInfo() {
        Bundle args = getArguments();
        if (args != null) {
            String userName = args.getString("user_name", "Customer");
            String userEmail = args.getString("user_email", "customer@email.com");

            tvUserName.setText(userName);
            tvUserEmail.setText(userEmail);
        }
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> {
            // Navigate back to launch screen
            Intent intent = new Intent(getActivity(), LaunchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
}