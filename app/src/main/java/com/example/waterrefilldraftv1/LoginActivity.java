package com.example.waterrefilldraftv1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * LoginActivity - User login screen
 * Authenticates users and navigates to dashboard
 * Provides forgot password functionality
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister;
    private UserDatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize database manager
        dbManager = new UserDatabaseManager(this);

        initViews();
        setupClickListeners();
    }

    /**
     * Initialize UI components
     */
    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvRegister = findViewById(R.id.tv_register);
    }

    /**
     * Setup click listeners for buttons and text views
     */
    private void setupClickListeners() {
        // Handle login
        btnLogin.setOnClickListener(v -> handleLogin());

        // Navigate to forgot password screen
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Navigate to register screen
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Handle user login process
     * Validates credentials and navigates to dashboard on success
     */
    private void handleLogin() {
        // Get input values
        String usernameOrEmail = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (!validateInput(usernameOrEmail, password)) {
            return;
        }

        // Attempt to login user
        UserDatabaseManager.User user = dbManager.loginUser(usernameOrEmail, password);

        if (user != null) {
            // Login successful
            Toast.makeText(this, "Welcome " + user.firstName + "!", Toast.LENGTH_SHORT).show();

            // Navigate to dashboard
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.putExtra("user_name", user.firstName + " " + user.lastName);
            intent.putExtra("user_email", user.email);
            startActivity(intent);
            finish(); // Close login activity
        } else {
            // Login failed
            Toast.makeText(this, "Invalid username/email or password!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Validate login input
     * @param usernameOrEmail Username or email input
     * @param password Password input
     * @return true if validation passes, false otherwise
     */
    private boolean validateInput(String usernameOrEmail, String password) {
        if (TextUtils.isEmpty(usernameOrEmail)) {
            etUsername.setError("Username or email is required");
            etUsername.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }
}