package com.example.waterrefilldraftv1.Login_Customer_and_Riders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox; // ✅ ADD THIS IMPORT
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

import com.example.waterrefilldraftv1.Riders.Utils.RiderAuthHelper;
import com.example.waterrefilldraftv1.Riders.Utils.RiderSessionStore;
import com.example.waterrefilldraftv1.Riders.models.Rider;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ImageView ivPasswordToggle;
    private TextView tvForgotPassword;
    private CheckBox cbRememberMe; // ✅ ADD CheckBox
    private NetworkManager networkManager;
    private ProgressDialog progressDialog;
    private boolean isPasswordVisible = false;
    private SessionStore sessionStore;
    private SharedPreferences loginPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_activity_login);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        networkManager = new NetworkManager(this);
        sessionStore = new SessionStore(this);
        loginPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        ivPasswordToggle = findViewById(R.id.iv_password_toggle);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        cbRememberMe = findViewById(R.id.cb_remember_me); // ✅ ADD CheckBox

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

        // ✅ Load saved credentials if "Remember Me" was checked
        loadSavedCredentials();
    }

    // ✅ ADD: Load saved credentials
    private void loadSavedCredentials() {
        boolean rememberMe = loginPrefs.getBoolean(KEY_REMEMBER_ME, false);
        if (rememberMe) {
            String savedUsername = loginPrefs.getString(KEY_USERNAME, "");
            String savedPassword = loginPrefs.getString(KEY_PASSWORD, "");

            etUsername.setText(savedUsername);
            etPassword.setText(savedPassword);
            cbRememberMe.setChecked(true);
        }
    }

    // ✅ ADD: Save credentials method
    private void saveCredentials(String username, String password) {
        SharedPreferences.Editor editor = loginPrefs.edit();
        editor.putBoolean(KEY_REMEMBER_ME, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    // ✅ ADD: Clear saved credentials method
    private void clearCredentials() {
        SharedPreferences.Editor editor = loginPrefs.edit();
        editor.putBoolean(KEY_REMEMBER_ME, false);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_PASSWORD);
        editor.apply();
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

                    if (role != null && role.equalsIgnoreCase("rider")) {
                        // ✅ MARK: User has logged in at least once
                        RiderAuthHelper.setUserHasLoggedInBefore(LoginActivity.this, true);

                        // ✅ SAVE OR CLEAR CREDENTIALS BASED ON CHECKBOX
                        if (cbRememberMe.isChecked()) {
                            saveCredentials(usernameOrEmail, password);
                        } else {
                            clearCredentials();
                        }

                        if (response.getToken() != null) {
                            sessionStore.saveToken(response.getToken());
                            TokenManager.saveToken(LoginActivity.this, response.getToken());
                        }

                        sessionStore.saveUser(response.getUser());

                        RiderSessionStore riderSessionStore = new RiderSessionStore(LoginActivity.this);
                        Rider rider = convertUserToRider(response.getUser());
                        riderSessionStore.saveRider(rider);

                        SharedPreferences riderPrefs = getSharedPreferences("RiderPrefs", MODE_PRIVATE);
                        riderPrefs.edit().putString("rider", new Gson().toJson(rider)).apply();

                        Toast.makeText(LoginActivity.this,
                                "Welcome " + response.getUser().getFirstName() + " (Rider)!",
                                Toast.LENGTH_SHORT).show();

                        Intent riderIntent = new Intent(LoginActivity.this, RiderDashboardActivity.class);
                        riderIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(riderIntent);
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Only riders can log in using the mobile app.",
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

            private Rider convertUserToRider(com.example.waterrefilldraftv1.Customer.models.User user) {
                Rider rider = new Rider();
                rider.setUser_id(user.getUserId());
                rider.setFirst_name(user.getFirstName());
                rider.setLast_name(user.getLastName());
                rider.setEmail(user.getEmail());
                rider.setContact_no(user.getContactNo());
                rider.setUsername(user.getUsername());
                rider.setRole(user.getRole());
                rider.setStatus(user.getStatus());
                return rider;
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();

                String userMessage;
                if (error.toLowerCase().contains("no internet") ||
                        error.toLowerCase().contains("network error") ||
                        error.toLowerCase().contains("unknownhost") ||
                        error.toLowerCase().contains("connectexception")) {
                    userMessage = "No internet connection. Please check your network and try again.";
                } else if (error.toLowerCase().contains("timeout")) {
                    userMessage = "Connection timeout. Please try again.";
                } else if (error.toLowerCase().contains("invalid username") ||
                        error.toLowerCase().contains("invalid password") ||
                        error.toLowerCase().contains("401")) {
                    userMessage = "Invalid username/email or password. Please try again.";
                } else if (error.toLowerCase().contains("account not found") ||
                        error.toLowerCase().contains("404")) {
                    userMessage = "Account not found. Please check your credentials.";
                } else if (error.toLowerCase().contains("server error") ||
                        error.toLowerCase().contains("500")) {
                    userMessage = "Server error. Please try again later.";
                } else {
                    userMessage = error; // Use the specific error message from server
                }

                Toast.makeText(LoginActivity.this, userMessage, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Login error: " + error);
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