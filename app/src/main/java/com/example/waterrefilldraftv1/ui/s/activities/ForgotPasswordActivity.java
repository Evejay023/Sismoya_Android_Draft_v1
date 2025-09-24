package com.example.waterrefilldraftv1.ui.s.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.models.ApiResponse;
import com.example.waterrefilldraftv1.network.NetworkManager;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button sendCodeButton;
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // ✅ Initialize views
        emailInput = findViewById(R.id.et_email);
        sendCodeButton = findViewById(R.id.btn_send_code);

        // ✅ Initialize NetworkManager
        networkManager = new NetworkManager(this);

        // ✅ Handle button click
        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ Call API
                networkManager.requestPasswordReset(email, new NetworkManager.ApiCallback<ApiResponse>() {
                    @Override
                    public void onSuccess(ApiResponse response) {
                        Toast.makeText(ForgotPasswordActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();

                        if (response.isSuccess()) {
                            // 👉 If success, go to VerifyCodeActivity
                            // Example navigation:
                            // Intent intent = new Intent(ForgotPasswordActivity.this, VerifyCodeActivity.class);
                            // intent.putExtra("email", email);
                            // startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(ForgotPasswordActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
