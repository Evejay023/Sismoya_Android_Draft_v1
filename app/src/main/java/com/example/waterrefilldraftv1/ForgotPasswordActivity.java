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
 * ForgotPasswordActivity - Password recovery screen
 * Allows users to request password reset by email
 * Generates and sends verification code for password recovery
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSendCode, btnBackToLogin;
    private UserDatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

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
        etEmail = findViewById(R.id.et_email);
        btnSendCode = findViewById(R.id.btn_send_code);
        btnBackToLogin = findViewById(R.id.btn_back_to_login);
    }

    /**
     * Setup click listeners for buttons
     */
    private void setupClickListeners() {
        // Handle send code request
        btnSendCode.setOnClickListener(v -> handleSendCode());

        // Navigate back to login screen
        btnBackToLogin.setOnClickListener(v -> {
            finish(); // Go back to previous screen
        });
    }

    /**
     * Handle send verification code process
     * Validates email and generates reset code
     */
    private void handleSendCode() {
        // Get email input
        String email = etEmail.getText().toString().trim();

        // Validate input
        if (!validateInput(email)) {
            return;
        }

        // Check if email exists in database
        if (dbManager.emailExists(email)) {
            // Generate reset code
            String resetCode = dbManager.generateResetCode(email);

            // In a real app, you would send this code via email
            // For now, we'll show it in a toast (for testing purposes)
            Toast.makeText(this, "Reset code sent! Code: " + resetCode, Toast.LENGTH_LONG).show();

            // Navigate to verify code screen
            Intent intent = new Intent(ForgotPasswordActivity.this, VerifyCodeActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Email not found! Please check your email address.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Validate email input
     * @param email Email input to validate
     * @return true if validation passes, false otherwise
     */
    private boolean validateInput(String email) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        return true;
    }
}