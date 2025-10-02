package com.example.waterrefilldraftv1.ui.s.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.network.NetworkManager;
import com.google.gson.Gson;
import com.example.waterrefilldraftv1.utils.SessionStore;
import com.example.waterrefilldraftv1.models.LoginResponse;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister;
    private ImageView ivPasswordToggle;
    private NetworkManager networkManager;
    private ProgressDialog progressDialog;
    private boolean isPasswordVisible = false;
    private SessionStore sessionStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        networkManager = new NetworkManager(this);
        sessionStore = new SessionStore(this);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvRegister = findViewById(R.id.tv_register);
        ivPasswordToggle = findViewById(R.id.iv_password_toggle);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        btnLogin.setOnClickListener(v -> handleLogin());

        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        ivPasswordToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivPasswordToggle.setImageResource(R.drawable.ic_eye_off);
                isPasswordVisible = false;
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivPasswordToggle.setImageResource(R.drawable.ic_eye_on);
                isPasswordVisible = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        String prefillEmail = getIntent().getStringExtra("prefill_email");
        if (prefillEmail != null) etUsername.setText(prefillEmail);
    }

    private void handleLogin() {
        String usernameOrEmail = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(usernameOrEmail)) {
            etUsername.setError("Username or email is required");
            etUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        progressDialog.show();

        networkManager.loginUser(usernameOrEmail, password, new NetworkManager.ApiCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                progressDialog.dismiss();

                Log.d(TAG, "Login Response received");
                Log.d(TAG, "Success: " + response.isSuccess());
                Log.d(TAG, "Message: " + response.getMessage());
                Log.d(TAG, "User: " + (response.getUser() != null ? response.getUser().getFirstName() : "null"));

                // ✅ MAIN FIX: Check success properly and ensure user exists
                if (response.isSuccess() && response.getUser() != null) {

                    Toast.makeText(LoginActivity.this,
                            "Welcome " + response.getUser().getFirstName() + "!",
                            Toast.LENGTH_SHORT).show();

                    // Save token & user for protected endpoints
                    if (response.getToken() != null) {
                        sessionStore.saveToken(response.getToken());
                    }
                    sessionStore.saveUser(response.getUser());

                    // ✅ Navigate to Dashboard with proper data
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);

                    // Convert user to JSON for passing to dashboard
                    Gson gson = new Gson();
                    String userJson = gson.toJson(response.getUser());
                    intent.putExtra("user_data", userJson);

                    Log.d(TAG, "Navigating to Dashboard with user data: " + userJson);

                    // Clear activity stack and start dashboard
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    // Login failed
                    String errorMessage = response.getMessage();
                    if (errorMessage == null || errorMessage.isEmpty()) {
                        errorMessage = "Login failed. Please check your credentials.";
                    }

                    Log.w(TAG, "Login failed: " + errorMessage);
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                Log.e(TAG, "Login error: " + error);

                // Show user-friendly error message
                String userMessage = "Login failed. Please check your internet connection and try again.";
                if (error.contains("401") || error.contains("Unauthorized")) {
                    userMessage = "Invalid username/email or password.";
                } else if (error.contains("500")) {
                    userMessage = "Server error. Please try again later.";
                }

                Toast.makeText(LoginActivity.this, userMessage, Toast.LENGTH_LONG).show();
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