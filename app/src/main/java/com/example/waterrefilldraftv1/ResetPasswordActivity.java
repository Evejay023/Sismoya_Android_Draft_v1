package com.example.waterrefilldraftv1;



import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;

/**
 * ResetPasswordActivity - Password reset screen
 * Allows users to set a new password after verification
 * Updates password in temporary database and shows success message
 */
public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmNewPassword;
    private Button btnResetPassword;
    private UserDatabaseManager dbManager;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize database manager
        dbManager = new UserDatabaseManager(this);

        // Get email from intent
        userEmail = getIntent().getStringExtra("email");

        initViews();
        setupClickListeners();
    }

    /**
     * Initialize UI components
     */
    private void initViews() {
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmNewPassword = findViewById(R.id.et_confirm_new_password);
        btnResetPassword = findViewById(R.id.btn_reset_password);
    }

    /**
     * Setup click listeners for buttons
     */
    private void setupClickListeners() {
        // Handle password reset
        btnResetPassword.setOnClickListener(v -> handleResetPassword());
    }

    /**
     * Handle password reset process
     * Validates new password and updates in database
     */
    private void handleResetPassword() {
        // Get input values
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

        // Validate input
        if (!validateInput(newPassword, confirmNewPassword)) {
            return;
        }

        // Reset password in database
        if (dbManager.resetPassword(userEmail, newPassword)) {
            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();

            // Show success screen briefly, then navigate to login
            showSuccessAndNavigate();
        } else {
            Toast.makeText(this, "Failed to reset password. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show success message and navigate to login screen
     */
    private void showSuccessAndNavigate() {
        // Create success intent
        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        // Delay navigation to show success message
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 2000); // 2 second delay
    }

    /**
     * Validate password input
     * @param newPassword New password input
     * @param confirmNewPassword Confirm password input
     * @return true if validation passes, false otherwise
     */
    private boolean validateInput(String newPassword, String confirmNewPassword) {
        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return false;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            etNewPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(confirmNewPassword)) {
            etConfirmNewPassword.setError("Please confirm your new password");
            etConfirmNewPassword.requestFocus();
            return false;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            etConfirmNewPassword.setError("Passwords do not match");
            etConfirmNewPassword.requestFocus();
            return false;
        }

        return true;
    }
}

