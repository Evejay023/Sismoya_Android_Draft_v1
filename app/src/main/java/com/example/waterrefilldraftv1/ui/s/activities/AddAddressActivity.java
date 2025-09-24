package com.example.waterrefilldraftv1.ui.s.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.waterrefilldraftv1.R;

public class AddAddressActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etLabel, etAddress;
    private Switch switchDefault;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        etLabel = findViewById(R.id.et_label);
        etAddress = findViewById(R.id.et_address);
        switchDefault = findViewById(R.id.switch_default);
        btnSave = findViewById(R.id.btn_save);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        btnSave.setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        String label = etLabel.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(label)) {
            etLabel.setError("Label is required");
            etLabel.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Address is required");
            etAddress.requestFocus();
            return;
        }

        // In a real app, you would save this to database/preferences
        // For now, we'll just return the new address
        String newAddress = label + ": " + address;

        Intent resultIntent = new Intent();
        resultIntent.putExtra("new_address", newAddress);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Address saved successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}