package com.example.waterrefilldraftv1.Customer.UserInterface.activities;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Customer.models.ApiResponse;
import com.example.waterrefilldraftv1.Customer.models.RegisterRequest;
import com.example.waterrefilldraftv1.Customer.network.ApiService;
import com.example.waterrefilldraftv1.Customer.network.NetworkManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etContactNo, etUsername, etPassword;
    private Button btnSignUp;
    private ImageView ivPasswordToggle;
    private TextView tvLogin;
    private ProgressBar progressBar;

    private NetworkManager networkManager;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        etContactNo = findViewById(R.id.et_contact_no);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        ivPasswordToggle = findViewById(R.id.iv_password_toggle);
        btnSignUp = findViewById(R.id.btn_sign_up);
        tvLogin = findViewById(R.id.tv_login);

        progressBar = new ProgressBar(this);
        networkManager = new NetworkManager(this);


        // Toggle password visibility
        ivPasswordToggle.setOnClickListener(v -> togglePasswordVisibility());

        // Login link
        tvLogin.setOnClickListener(v -> finish());

        // Sign-up button
        btnSignUp.setOnClickListener(v -> handleRegister());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivPasswordToggle.setImageResource(R.drawable.ic_eye_off);
        } else {
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivPasswordToggle.setImageResource(R.drawable.ic_eye_on);
        }
        etPassword.setSelection(etPassword.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    private void handleRegister() {
        String firstname = etFirstName.getText().toString().trim();
        String lastname = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etContactNo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        registerUser(firstname, lastname, email, phone, password);
    }

    private void registerUser(String firstname, String lastname, String email, String phone, String password) {
        btnSignUp.setEnabled(false);

        // Create a user model (based on your backend)
        com.example.waterrefilldraftv1.Customer.models.User user =
                new com.example.waterrefilldraftv1.Customer.models.User(firstname, lastname, email, phone, password);

        networkManager.registerUser(user, new NetworkManager.ApiCallback<ApiResponse>() {
            @Override
            public void onSuccess(ApiResponse response) {
                btnSignUp.setEnabled(true);
                if (!response.isError()) {
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                btnSignUp.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Registration failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
