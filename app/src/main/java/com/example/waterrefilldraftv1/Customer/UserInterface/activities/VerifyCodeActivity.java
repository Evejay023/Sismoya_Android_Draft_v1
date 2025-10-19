package com.example.waterrefilldraftv1.Customer.UserInterface.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Customer.models.ApiResponse;
import com.example.waterrefilldraftv1.Customer.network.NetworkManager;

public class VerifyCodeActivity extends AppCompatActivity {

    private EditText etCode;
    private Button btnVerify;
    private TextView tvResend;
    private ProgressDialog progressDialog;
    private NetworkManager networkManager;

    private String userEmail; // passed from ForgotPasswordActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Get email from previous screen
        userEmail = getIntent().getStringExtra("email");

        networkManager = new NetworkManager(this);
        initViews();
        setupProgressDialog();
        setupClickListeners();
    }

    private void initViews() {
        etCode = findViewById(R.id.et_code);
        btnVerify = findViewById(R.id.btn_verify);
        tvResend = findViewById(R.id.tv_didnt_receive_code);
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying code...");
        progressDialog.setCancelable(false);
    }

    private void setupClickListeners() {
        btnVerify.setOnClickListener(v -> handleVerify());

        tvResend.setOnClickListener(v -> {
            progressDialog.show();
            networkManager.requestPasswordReset(userEmail, new NetworkManager.ApiCallback<ApiResponse>() {
                @Override
                public void onSuccess(ApiResponse response) {
                    progressDialog.dismiss();
                    Toast.makeText(VerifyCodeActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    progressDialog.dismiss();
                    Toast.makeText(VerifyCodeActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void handleVerify() {
        String code = etCode.getText().toString().trim();

        if (TextUtils.isEmpty(code)) {
            etCode.setError("Code is required");
            return;
        }

        progressDialog.show();

        networkManager.verifyCode(userEmail, code, new NetworkManager.ApiCallback<ApiResponse>() {
            @Override
            public void onSuccess(ApiResponse response) {
                progressDialog.dismiss();

                if (response.isSuccess()) {
                    Toast.makeText(VerifyCodeActivity.this, "Code verified!", Toast.LENGTH_SHORT).show();

                    // Navigate to ResetPasswordActivity
                    Intent intent = new Intent(VerifyCodeActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("email", userEmail);
                    intent.putExtra("code", code);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(VerifyCodeActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                Toast.makeText(VerifyCodeActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
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
