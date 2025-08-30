package com.example.waterrefilldraftv1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * LaunchActivity - Welcome screen with app introduction
 * Displays water delivery message and navigation buttons
 * Users can choose to Login or Register from this screen
 */
public class LaunchActivity extends AppCompatActivity {

    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

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
        btnRegister = findViewById(R.id.btn_register);
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

        // Navigate to Register screen
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LaunchActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}