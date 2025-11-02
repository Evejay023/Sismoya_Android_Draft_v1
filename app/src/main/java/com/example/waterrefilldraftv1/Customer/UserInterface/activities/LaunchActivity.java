package com.example.waterrefilldraftv1.Customer.UserInterface.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.waterrefilldraftv1.Login_Customer_and_Riders.LoginActivity;
import com.example.waterrefilldraftv1.R;

/**
 * LaunchActivity - Welcome screen with app introduction
 * Displays water delivery message and navigation buttons
 * Users can choose to Login or Register from this screen
 */
public class LaunchActivity extends AppCompatActivity {

    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_activity_launch);

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
        btnLogin = findViewById(R.id.btn_login);

    }

    /**
     * Setup button click listeners
     */
    private void setupClickListeners() {
        // Navigate to Login screen
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
            startActivity(intent);
        });


    }
}