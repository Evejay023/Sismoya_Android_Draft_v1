package com.example.waterrefilldraftv1.Riders.UserInterrface.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.models.Rider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Rider_Fragment_Dashboard extends Fragment {

    private TextView tvRiderName, tvDate, tvTime;
    private Button btnViewPickup1, btnViewDeliver1;
    private Rider currentRider;

    public static Rider_Fragment_Dashboard newInstance(Rider rider) {
        Rider_Fragment_Dashboard fragment = new Rider_Fragment_Dashboard();
        Bundle args = new Bundle();
        if (rider != null) args.putSerializable("rider_data", rider);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rider_fragment_dashboard, container, false);

        tvRiderName = view.findViewById(R.id.tv_rider_name);
        tvDate = view.findViewById(R.id.tv_date);
        tvTime = view.findViewById(R.id.tv_time);
        btnViewPickup1 = view.findViewById(R.id.btn_view_pickup_1);
        btnViewDeliver1 = view.findViewById(R.id.btn_view_deliver_1);

        if (getArguments() != null) {
            currentRider = (Rider) getArguments().getSerializable("rider_data");
            if (currentRider != null) {
                tvRiderName.setText(currentRider.getFullName());
            } else {
                tvRiderName.setText("Rider");
            }
        }

        displayDate();
        displayLiveTime();

        btnViewPickup1.setOnClickListener(v -> {
            openFragment(Rider_Fragment_To_PickUp.newInstance(currentRider));
            Toast.makeText(requireContext(), "Opening pick-up list...", Toast.LENGTH_SHORT).show();
        });

        btnViewDeliver1.setOnClickListener(v -> {
            openFragment(Rider_Fragment_To_Deliver.newInstance(currentRider));
            Toast.makeText(requireContext(), "Opening delivery list...", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void displayDate() {
        String currentDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
        tvDate.setText(currentDate);
    }

    private void displayLiveTime() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                tvTime.setText(currentTime);
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    // ✅ Called by RiderDashboardActivity when SharedPreferences update
    public void updateRiderName(String newName) {
        if (tvRiderName != null && newName != null && !newName.trim().isEmpty()) {
            tvRiderName.setText(newName);
        }
    }
}
