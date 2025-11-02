package com.example.waterrefilldraftv1.Login_Customer_and_Riders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Global.network.ApiResponse;
import com.example.waterrefilldraftv1.Global.network.NetworkManager;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmPassword;
    private Button btnResetPassword;
    private NetworkManager networkManager;
    private ProgressDialog progressDialog;
    private String userEmail, verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Get data from intent
        userEmail = getIntent().getStringExtra("email");
        verificationCode = getIntent().getStringExtra("code");

        networkManager = new NetworkManager(this);
        initViews();
        setupClickListeners();
        setupProgressDialog();
    }

    private void initViews() {
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_new_password);
        btnResetPassword = findViewById(R.id.btn_reset_password);
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Resetting password...");
        progressDialog.setCancelable(false);
    }

    private void setupClickListeners() {
        btnResetPassword.setOnClickListener(v -> handleResetPassword());
    }

    private void handleResetPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateInput(newPassword, confirmPassword)) {
            return;
        }

        progressDialog.show();

        networkManager.resetPassword(userEmail, verificationCode, newPassword, new NetworkManager.ApiCallback<ApiResponse>() {
            @Override
            public void onSuccess(ApiResponse response) {
                progressDialog.dismiss();

                if (response.isSuccess()) {
                    Toast.makeText(ResetPasswordActivity.this, "Password reset successful! Please login with your new password.", Toast.LENGTH_LONG).show();

                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        intent.putExtra("prefill_email", userEmail);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }, 2000);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                Toast.makeText(ResetPasswordActivity.this, "Reset error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput(String newPassword, String confirmPassword) {
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

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (networkManager != null) {
            networkManager.shutdown();
        }
    }
}