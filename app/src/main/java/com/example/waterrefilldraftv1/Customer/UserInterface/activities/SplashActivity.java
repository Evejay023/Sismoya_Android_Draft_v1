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

    private static final int SPLASH_DURATION = 3000;
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_splash_screen);

        TokenManager.init(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        new Handler().postDelayed(() -> {
            checkAuthenticationStatus();
        }, SPLASH_DURATION);
    }

    private void checkAuthenticationStatus() {
        boolean shouldDirectToLogin = RiderAuthHelper.shouldDirectToLogin(this);
        boolean isRiderLoggedIn = RiderAuthHelper.isRiderLoggedIn(this);
        boolean hasLoggedInBefore = RiderAuthHelper.hasUserLoggedInBefore(this);

        Log.d(TAG, "🔍 AUTH STATUS CHECK:");
        Log.d(TAG, "  - shouldDirectToLogin: " + shouldDirectToLogin);
        Log.d(TAG, "  - isRiderLoggedIn: " + isRiderLoggedIn);
        Log.d(TAG, "  - hasLoggedInBefore: " + hasLoggedInBefore);

        // ✅ CHECK 1: Direct login flag (after logout)
        if (shouldDirectToLogin) {
            Log.d(TAG, "✅ DIRECT TO LOGIN FLAG SET - Going to LoginActivity");
            RiderAuthHelper.clearDirectToLoginFlag(this);
            redirectToLogin();
        }
        // ✅ CHECK 2: Actually logged in (valid token + rider data)
        else if (isRiderLoggedIn) {
            Log.d(TAG, "✅ USER IS LOGGED IN - Going to Dashboard");
            redirectToRiderDashboard();
        }
        // ✅ CHECK 3: User has logged in before (but not currently logged in)
        else if (hasLoggedInBefore) {
            Log.d(TAG, "🔵 USER HAS LOGGED IN BEFORE - Going to LoginActivity");
            redirectToLogin();
        }
        // ✅ CHECK 4: New user (never logged in before)
        else {
            Log.d(TAG, "❌ NEW USER - Going to GetStarted");
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