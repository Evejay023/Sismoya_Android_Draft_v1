package com.example.waterrefilldraftv1.Customer.UserInterface.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.waterrefilldraftv1.R;

/**
 * GetStartedActivity - Introduction screen after splash
 * Shows app introduction with a single "Get Started" button
 * Bridge between splash screen and main authentication flow
 */
public class GetStartedActivity extends AppCompatActivity {

    private Button btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_activity_get_started);

        // Hide action bar for clean look
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupClickListeners();
    }

    /**
     * Initialize UI components
     */
    private void initViews() {
        btnGetStarted = findViewById(R.id.btn_get_started);
    }

    /**
     * Setup button click listener
     */
    private void setupClickListeners() {
        // Navigate to Launch screen when Get Started is clicked
        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(GetStartedActivity.this, LaunchActivity.class);
            startActivity(intent);
            finish(); // Close get started activity
        });
    }

    /**
     * Handle back button - close app instead of going back to splash
     */
    @Override
    public void onBackPressed() {
        // Close the app when back is pressed on get started screen
        super.onBackPressed();
        finishAffinity();
    }
}