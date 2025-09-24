package com.example.waterrefilldraftv1.ui.s.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.waterrefilldraftv1.models.ApiResponse;
import com.example.waterrefilldraftv1.models.User;
import com.example.waterrefilldraftv1.network.NetworkManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etContactNo, etUsername, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvLogin;
    private ImageView ivPasswordToggle, ivConfirmPasswordToggle;
    private NetworkManager networkManager;
    private ProgressDialog progressDialog;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        networkManager = new NetworkManager(this);

        initViews();
        setupClickListeners();
        setupPasswordToggle();
        setupProgressDialog();
    }

    private void initViews() {
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        etContactNo = findViewById(R.id.et_contact_no);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignUp = findViewById(R.id.btn_sign_up);
        tvLogin = findViewById(R.id.tv_login);
        ivPasswordToggle = findViewById(R.id.iv_password_toggle);
        ivConfirmPasswordToggle = findViewById(R.id.iv_confirm_password_toggle);
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating account...");
        progressDialog.setCancelable(false);
    }

    private void setupClickListeners() {
        btnSignUp.setOnClickListener(v -> handleRegistration());

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupPasswordToggle() {
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

        ivConfirmPasswordToggle.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivConfirmPasswordToggle.setImageResource(R.drawable.ic_eye_off);
                isConfirmPasswordVisible = false;
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivConfirmPasswordToggle.setImageResource(R.drawable.ic_eye_on);
                isConfirmPasswordVisible = true;
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });
    }

    private void handleRegistration() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String contactNo = etContactNo.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateInput(firstName, lastName, email, contactNo, username, password, confirmPassword)) {
            return;
        }

        progressDialog.show();

        User newUser = new User(firstName, lastName, email, contactNo, username, password, "user");

        networkManager.registerUser(newUser, new NetworkManager.ApiCallback<ApiResponse>() {
            @Override
            public void onSuccess(ApiResponse response) {
                progressDialog.dismiss();

                Log.d("Registration", "Response: " + response.isSuccess() + ", Message: " + response.getMessage());

                if (response.isSuccess()) {
                    Toast.makeText(RegisterActivity.this, "Registration successful! Please login.", Toast.LENGTH_LONG).show();

                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.putExtra("prefill_email", email);
                        startActivity(intent);
                        finish();
                    }, 1500);
                } else {
                    Toast.makeText(RegisterActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                Log.e("Registration", "Error: " + error);
                Toast.makeText(RegisterActivity.this, "Registration failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInput(String firstName, String lastName, String email,
                                  String contactNo, String username, String password, String confirmPassword) {
        if (TextUtils.isEmpty(firstName)) { etFirstName.setError("First name is required"); etFirstName.requestFocus(); return false; }
        if (TextUtils.isEmpty(lastName)) { etLastName.setError("Last name is required"); etLastName.requestFocus(); return false; }
        if (TextUtils.isEmpty(email)) { etEmail.setError("Email is required"); etEmail.requestFocus(); return false; }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { etEmail.setError("Please enter a valid email"); etEmail.requestFocus(); return false; }
        if (TextUtils.isEmpty(contactNo)) { etContactNo.setError("Contact number is required"); etContactNo.requestFocus(); return false; }
        if (TextUtils.isEmpty(username)) { etUsername.setError("Username is required"); etUsername.requestFocus(); return false; }
        if (TextUtils.isEmpty(password)) { etPassword.setError("Password is required"); etPassword.requestFocus(); return false; }
        if (password.length() < 6) { etPassword.setError("Password must be at least 6 characters"); etPassword.requestFocus(); return false; }
        if (TextUtils.isEmpty(confirmPassword)) { etConfirmPassword.setError("Please confirm your password"); etConfirmPassword.requestFocus(); return false; }
        if (!password.equals(confirmPassword)) { etConfirmPassword.setError("Passwords do not match"); etConfirmPassword.requestFocus(); return false; }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        if (networkManager != null) networkManager.shutdown();
    }
}
