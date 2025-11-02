package com.example.waterrefilldraftv1.Riders.UserInterrface.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.waterrefilldraftv1.Global.network.ApiResponse;
import com.example.waterrefilldraftv1.Global.network.ApiService;
import com.example.waterrefilldraftv1.Global.network.RetrofitClient;
import com.example.waterrefilldraftv1.Global.network.TokenManager;
import com.example.waterrefilldraftv1.Login_Customer_and_Riders.LoginActivity;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.UserInterrface.Activities.Rider_PersonalInformationActivity;
import com.example.waterrefilldraftv1.Riders.UserInterrface.Activities.Rider_Change_Password_Activity;
import com.example.waterrefilldraftv1.Riders.models.Rider;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Rider_Fragment_Profile extends Fragment {

    private static final String PREF_NAME = "RiderPrefs";
    private static final String RIDER_KEY = "rider";

    private TextView tvUserName, tvUserEmail, tvUserPhone;
    private LinearLayout llInformation, llDeliveries, llChangePassword, llLogout;
    private SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    // ✅ Receiver listens for profile updates
    private final BroadcastReceiver riderUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.waterrefilldraftv1.RIDER_PROFILE_UPDATED".equals(intent.getAction())) {
                updateFromPrefs();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rider_fragment_profile, container, false);

        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        tvUserPhone = view.findViewById(R.id.tv_user_phone);
        llInformation = view.findViewById(R.id.ll_information);
        llDeliveries = view.findViewById(R.id.btn_deliveries);
        llChangePassword = view.findViewById(R.id.btn_change_password);
        llLogout = view.findViewById(R.id.btn_logout);

        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        updateFromPrefs(); // load initial data
        setupClickListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // ✅ Register receiver safely
        IntentFilter filter = new IntentFilter("com.example.waterrefilldraftv1.RIDER_PROFILE_UPDATED");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(riderUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            ContextCompat.registerReceiver(requireActivity(), riderUpdateReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
        }
        updateFromPrefs();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(riderUpdateReceiver);
    }

    public void updateFromPrefs() {
        String json = sharedPreferences.getString(RIDER_KEY, null);
        if (json != null) {
            Rider rider = gson.fromJson(json, Rider.class);
            updateUi(rider);
        }
    }

    private void updateUi(Rider rider) {
        if (rider == null) return;
        String fullName = (rider.getFirstName() + " " + rider.getLastName()).trim();
        tvUserName.setText(fullName.isEmpty() ? "Rider Name" : fullName);
        tvUserEmail.setText(rider.getEmail() != null ? rider.getEmail() : "rider@email.com");
        tvUserPhone.setText(rider.getContactNo() != null ? rider.getContactNo() : "+639000000000");
    }

    private void setupClickListeners() {
        llInformation.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), Rider_PersonalInformationActivity.class)));
        llDeliveries.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Opening deliveries soon...", Toast.LENGTH_SHORT).show());
        llChangePassword.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), Rider_Change_Password_Activity.class)));
        llLogout.setOnClickListener(v -> logoutRider());
    }

    private void logoutRider() {
        sharedPreferences.edit().clear().apply();
        TokenManager.clearToken();
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
