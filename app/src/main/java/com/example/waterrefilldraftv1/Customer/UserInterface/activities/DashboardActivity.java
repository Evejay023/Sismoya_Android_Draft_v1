package com.example.waterrefilldraftv1.Customer.UserInterface.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.waterrefilldraftv1.Customer.UserInterface.fragment.DashboardFragment;
import com.example.waterrefilldraftv1.Customer.UserInterface.fragment.ProfileFragment;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Customer.models.User;
import com.example.waterrefilldraftv1.Customer.UserInterface.fragment.ContainersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

public class DashboardActivity extends AppCompatActivity {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        String userJson = getIntent().getStringExtra("user_data");
        if (userJson != null) {
            currentUser = new Gson().fromJson(userJson, User.class);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                openFragment(DashboardFragment.newInstance(currentUser));
                return true;
            } else if (id == R.id.nav_containers) {
                openFragment(new ContainersFragment());
                return true;
            } else if (id == R.id.nav_orders) {
                openFragment(new com.example.waterrefilldraftv1.Customer.UserInterface.fragment.OrdersFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                openFragment(ProfileFragment.newInstance(currentUser));
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            openFragment(DashboardFragment.newInstance(currentUser));
        }
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}