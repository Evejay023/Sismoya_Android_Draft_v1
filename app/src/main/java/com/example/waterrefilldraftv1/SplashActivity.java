package com.example.waterrefilldraftv1;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.waterrefilldraftv1.GetStartedActivity;
import com.example.waterrefilldraftv1.R;

/**
 * SplashActivity - Initial screen that displays app logo
 * Shows for 3 seconds then navigates to LaunchActivity
 * Uses custom gradient background matching the design
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Hide action bar for full screen experience
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Navigate to GetStartedActivity after splash duration
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, GetStartedActivity.class);
            startActivity(intent);
            finish(); // Close splash activity
        }, SPLASH_DURATION);
    }
}