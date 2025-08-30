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
 * RegisterActivity - User registration screen
 * Collects user information and creates new account
 * Validates input and stores user data in temporary database
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etContactNo, etUsername, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvLogin;
    private UserDatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_register);

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
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        etContactNo = findViewById(R.id.et_contact_no);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignUp = findViewById(R.id.btn_sign_up);
        tvLogin = findViewById(R.id.tv_login);
    }

    /**
     * Setup click listeners for buttons and text views
     */
    private void setupClickListeners() {
        // Handle registration
        btnSignUp.setOnClickListener(v -> handleRegistration());

        // Navigate to login screen
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Handle user registration process
     * Validates input and creates new user account
     */
    private void handleRegistration() {
        // Get input values
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String contactNo = etContactNo.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate input
        if (!validateInput(firstName, lastName, email, contactNo, username, password, confirmPassword)) {
            return;
        }

        // Create user object
        UserDatabaseManager.User newUser = new UserDatabaseManager.User(
                firstName, lastName, email, contactNo, username, password
        );

        // Attempt to register user
        if (dbManager.registerUser(newUser)) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

            // Navigate to login screen
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Username or email already exists!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Validate registration input
     * @return true if all validations pass, false otherwise
     */
    private boolean validateInput(String firstName, String lastName, String email,
                                  String contactNo, String username, String password, String confirmPassword) {

        // Check for empty fields
        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("First name is required");
            etFirstName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Last name is required");
            etLastName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(contactNo)) {
            etContactNo.setError("Contact number is required");
            etContactNo.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        // Check password length
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return false;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }
}