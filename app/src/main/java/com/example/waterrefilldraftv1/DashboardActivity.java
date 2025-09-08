package com.example.waterrefilldraftv1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * DashboardActivity - Main container with bottom navigation
 * Manages fragments: Dashboard, Containers, Orders, Profile
 */
public class DashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvUserEmail;
    private BottomNavigationView bottomNav;
    private String userName, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Get user data from intent
        userName = getIntent().getStringExtra("user_name");
        userEmail = getIntent().getStringExtra("user_email");
        if (userName == null) userName = "Customer";
        if (userEmail == null) userEmail = "customer@email.com";

        initViews();
        setupBottomNavigation();

        // Load default fragment (Dashboard)
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }
    }

    private void initViews() {
        bottomNav = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (itemId == R.id.nav_containers) {
                selectedFragment = new ContainersFragment();
            } else if (itemId == R.id.nav_orders) {
                selectedFragment = new OrdersFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                // Pass user data to fragments
                Bundle bundle = new Bundle();
                bundle.putString("user_name", userName);
                bundle.putString("user_email", userEmail);
                selectedFragment.setArguments(bundle);

                return loadFragment(selectedFragment);
            }
            return false;
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
}