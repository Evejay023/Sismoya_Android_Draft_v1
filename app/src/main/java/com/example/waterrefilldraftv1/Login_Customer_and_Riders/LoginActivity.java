package com.example.waterrefilldraftv1.Login_Customer_and_Riders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterrefilldraftv1.Customer.models.LoginResponse;
import com.example.waterrefilldraftv1.Global.network.TokenManager;
import com.example.waterrefilldraftv1.Customer.utils.SessionStore;
import com.example.waterrefilldraftv1.Global.network.NetworkManager;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.UserInterrface.Activities.RiderDashboardActivity;

import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ImageView ivPasswordToggle;
    private TextView tvForgotPassword;
    private NetworkManager networkManager;
    private ProgressDialog progressDialog;
    private boolean isPasswordVisible = false;
    private SessionStore sessionStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_activity_login);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        networkManager = new NetworkManager(this);
        sessionStore = new SessionStore(this);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        ivPasswordToggle = findViewById(R.id.iv_password_toggle);
        tvForgotPassword = findViewById(R.id.tv_forgot_password); // ✅ Added Forgot Password TextView

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        btnLogin.setOnClickListener(v -> handleLogin());
        ivPasswordToggle.setOnClickListener(v -> togglePasswordVisibility());

        // ✅ Forgot Password Click
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivPasswordToggle.setImageResource(R.drawable.ic_eye_off);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivPasswordToggle.setImageResource(R.drawable.ic_eye_on);
        }
        isPasswordVisible = !isPasswordVisible;
        etPassword.setSelection(etPassword.getText().length());
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

                if (response.isSuccess() && response.getUser() != null) {
                    String role = response.getUser().getRole();

                    // ✅ Allow only riders
                    if (role != null && role.equalsIgnoreCase("rider")) {
                        if (response.getToken() != null) {
                            sessionStore.saveToken(response.getToken());
                            TokenManager.saveToken(LoginActivity.this, response.getToken());
                        }

                        sessionStore.saveUser(response.getUser());
                        Gson gson = new Gson();
                        String userJson = gson.toJson(response.getUser());

                        Toast.makeText(LoginActivity.this,
                                "Welcome " + response.getUser().getFirstName() + " (Rider)!",
                                Toast.LENGTH_SHORT).show();

                        Intent riderIntent = new Intent(LoginActivity.this, RiderDashboardActivity.class);
                        riderIntent.putExtra("rider_data", userJson);
                        riderIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(riderIntent);
                    } else {
                        // ❌ Reject customers or other roles
                        Toast.makeText(LoginActivity.this,
                                " Only riders can log in using the mobile app.",
                                Toast.LENGTH_LONG).show();
                    }

                } else {
                    String errorMessage = response.getMessage();
                    if (errorMessage == null || errorMessage.isEmpty()) {
                        errorMessage = "Login failed. Please check your credentials.";
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                String userMessage = "Login failed. Please check your internet connection.";
                if (error.contains("401")) userMessage = "Invalid username/email or password.";
                else if (error.contains("500")) userMessage = "Server error. Try again later.";
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
