package com.example.waterrefilldraftv1.Riders.UserInterrface.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.UserInterrface.Fragment.Rider_Fragment_Dashboard;
import com.example.waterrefilldraftv1.Riders.UserInterrface.Fragment.Rider_Fragment_Delivery_History;
import com.example.waterrefilldraftv1.Riders.UserInterrface.Fragment.Rider_Fragment_Profile;
import com.example.waterrefilldraftv1.Riders.UserInterrface.Fragment.Rider_Fragment_To_Deliver;
import com.example.waterrefilldraftv1.Riders.UserInterrface.Fragment.Rider_Fragment_To_PickUp;
import com.example.waterrefilldraftv1.Riders.models.Rider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

public class RiderDashboardActivity extends AppCompatActivity {

    private Rider currentRider;
    private final Gson gson = new Gson();
    private SharedPreferences sharedPreferences;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_activity_main);

        sharedPreferences = getSharedPreferences("RiderPrefs", MODE_PRIVATE);

        // ✅ Load rider data from intent or saved prefs
        String riderJson = getIntent().getStringExtra("rider_data");
        if (riderJson != null) {
            currentRider = gson.fromJson(riderJson, Rider.class);
            saveRiderToPrefs(currentRider);
        } else {
            currentRider = loadRiderFromPrefs();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                openFragment(Rider_Fragment_Dashboard.newInstance(currentRider));
                return true;
            }

            else if (id == R.id.nav_to_pickup) {
                openFragment(Rider_Fragment_To_PickUp.newInstance(currentRider));
                return true;
            }

            else if (id == R.id.nav_to_deliver) {
                openFragment(Rider_Fragment_To_Deliver.newInstance(currentRider));
                return true;
            }

            else if (id == R.id.nav_delivery_history) {
                openFragment(Rider_Fragment_Delivery_History.newInstance(currentRider));
                return true;
            }

            else if (id == R.id.nav_profile) {
                openFragment(new Rider_Fragment_Profile());
                return true;
            }

            return false;
        });

        // ✅ Default open
        if (savedInstanceState == null) {
            openFragment(Rider_Fragment_Dashboard.newInstance(currentRider));
            bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // ✅ Always refresh latest saved rider
        Rider updatedRider = loadRiderFromPrefs();
        if (updatedRider != null) {
            currentRider = updatedRider;
        }

        // ✅ Refresh visible fragment UI immediately
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (current instanceof Rider_Fragment_Dashboard) {
            ((Rider_Fragment_Dashboard) current).updateRiderName(currentRider.getFullName());
        }
        else if (current instanceof Rider_Fragment_Profile) {
            ((Rider_Fragment_Profile) current).updateFromPrefs();
        }
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void saveRiderToPrefs(Rider rider) {
        if (rider == null) return;
        sharedPreferences.edit().putString("rider", gson.toJson(rider)).apply();
    }

    private Rider loadRiderFromPrefs() {
        String json = sharedPreferences.getString("rider", null);
        if (json != null) {
            try {
                return gson.fromJson(json, Rider.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}