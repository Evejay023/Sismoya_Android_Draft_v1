package com.example.waterrefilldraftv1.Customer.UserInterface.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.waterrefilldraftv1.Global.network.TokenManager;
import com.example.waterrefilldraftv1.Login_Customer_and_Riders.LoginActivity;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.UserInterrface.Activities.RiderDashboardActivity;
import com.example.waterrefilldraftv1.Riders.Utils.RiderAuthHelper;

/**
 * SplashActivity - Initial screen that checks authentication status
 * Shows for 3 seconds then navigates based on login status
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_splash_screen);

        // Initialize TokenManager with context
        TokenManager.init(this);

        // Hide action bar for full screen experience
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Navigate based on authentication status after splash duration
        new Handler().postDelayed(() -> {
            checkAuthenticationStatus();
        }, SPLASH_DURATION);
    }

    /**
     * Check if rider is already logged in using your existing RiderAuthHelper
     */
    private void checkAuthenticationStatus() {
        // ✅ CHECK: If we should go directly to login (after logout)
        if (RiderAuthHelper.shouldDirectToLogin(this)) {
            Log.d(TAG, "✅ DIRECT TO LOGIN FLAG SET - Going to LoginActivity");
            RiderAuthHelper.clearDirectToLoginFlag(this); // Clear the flag
            redirectToLogin();
        }
        // ✅ CHECK: If rider is already logged in
        else if (RiderAuthHelper.isRiderLoggedIn(this)) {
            Log.d(TAG, "✅ USER IS LOGGED IN - Going to Dashboard");
            redirectToRiderDashboard();
        }
        // ✅ DEFAULT: New user flow
        else {
            Log.d(TAG, "❌ USER NOT LOGGED IN - Going to GetStarted");
            redirectToGetStarted();
        }
    }

    /**
     * Redirect to login activity (after logout)
     */
    private void redirectToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close splash activity
    }

    /**
     * Redirect to rider dashboard
     */
    private void redirectToRiderDashboard() {
        Intent intent = new Intent(SplashActivity.this, RiderDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Close splash activity
    }

    /**
     * Redirect to get started flow for new users
     */
    private void redirectToGetStarted() {
        Intent intent = new Intent(SplashActivity.this, GetStartedActivity.class);
        startActivity(intent);
        finish(); // Close splash activity
    }
}