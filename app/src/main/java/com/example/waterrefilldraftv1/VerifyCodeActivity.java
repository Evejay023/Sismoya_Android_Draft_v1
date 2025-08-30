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
 * VerifyCodeActivity - Code verification screen
 * Verifies the reset code sent to user's email
 * Proceeds to password reset on successful verification
 */
public class VerifyCodeActivity extends AppCompatActivity {

    private EditText etCode;
    private Button btnVerify;
    private TextView tvDidntReceiveCode;
    private UserDatabaseManager dbManager;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

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
        etCode = findViewById(R.id.et_code);
        btnVerify = findViewById(R.id.btn_verify);
        tvDidntReceiveCode = findViewById(R.id.tv_didnt_receive_code);
    }

    /**
     * Setup click listeners for buttons and text views
     */
    private void setupClickListeners() {
        // Handle code verification
        btnVerify.setOnClickListener(v -> handleVerifyCode());

        // Handle resend code request
        tvDidntReceiveCode.setOnClickListener(v -> handleResendCode());
    }

    /**
     * Handle verification code validation
     * Checks if entered code matches the generated code
     */
    private void handleVerifyCode() {
        // Get code input
        String code = etCode.getText().toString().trim();

        // Validate input
        if (!validateInput(code)) {
            return;
        }

        // Verify the code
        if (dbManager.verifyResetCode(userEmail, code)) {
            Toast.makeText(this, "Code verified successfully!", Toast.LENGTH_SHORT).show();

            // Navigate to reset password screen
            Intent intent = new Intent(VerifyCodeActivity.this, ResetPasswordActivity.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid or expired code. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle resend code request
     * Generates and sends a new reset code
     */
    private void handleResendCode() {
        // Generate new reset code
        String resetCode = dbManager.generateResetCode(userEmail);

        // Show new code (in real app, this would be sent via email)
        Toast.makeText(this, "New code sent! Code: " + resetCode, Toast.LENGTH_LONG).show();
    }

    /**
     * Validate code input
     *
     * @param code Code input to validate
     * @return true if validation passes, false otherwise
     */
    private boolean validateInput(String code) {
        if (TextUtils.isEmpty(code)) {
            etCode.setError("Verification code is required");
            etCode.requestFocus();
            return false;
        }

        if (code.length() != 6) {
            etCode.setError("Code must be 6 digits");
            etCode.requestFocus();
            return false;
        }

        return true;
    }

}