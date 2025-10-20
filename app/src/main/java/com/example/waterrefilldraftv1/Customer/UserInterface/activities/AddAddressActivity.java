package com.example.waterrefilldraftv1.Customer.UserInterface.activities;

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
import com.example.waterrefilldraftv1.Customer.models.Address;
import com.example.waterrefilldraftv1.Customer.models.ApiResponse;
import com.example.waterrefilldraftv1.Customer.network.NetworkManager;

public class AddAddressActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etLabel, etAddress;
    private Switch switchDefault;
    private Button btnSave;
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_activity_add_address);

        initViews();
        setupClickListeners();
        networkManager = new NetworkManager(this);
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

        btnSave.setEnabled(false);

        Address body = new Address();
        body.setLabel(label);
        body.setAddress(address);
        body.setDefault(switchDefault.isChecked());

        networkManager.createAddress(body, new NetworkManager.ApiCallback<ApiResponse>() {
            @Override
            public void onSuccess(ApiResponse response) {
                btnSave.setEnabled(true);
                if (response.isSuccess()) {
                    String newAddress = label + ": " + address;
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("new_address", newAddress);
                    setResult(RESULT_OK, resultIntent);
                    Toast.makeText(AddAddressActivity.this, "Address saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddAddressActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String error) {
                btnSave.setEnabled(true);
                Toast.makeText(AddAddressActivity.this, "Save failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}