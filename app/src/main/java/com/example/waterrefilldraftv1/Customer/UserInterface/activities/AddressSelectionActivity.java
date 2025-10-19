package com.example.waterrefilldraftv1.Customer.UserInterface.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Customer.models.Address;
import com.example.waterrefilldraftv1.Customer.network.NetworkManager;
import android.widget.Toast;

public class AddressSelectionActivity extends AppCompatActivity {

    private ImageView ivBack;
    private CardView cardHomeAddress, cardOfficeAddress, cardAddAddress;
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_selection);

        initViews();
        setupClickListeners();
        networkManager = new NetworkManager(this);
        loadAddresses();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        cardHomeAddress = findViewById(R.id.card_home_address);
        cardOfficeAddress = findViewById(R.id.card_office_address);
        cardAddAddress = findViewById(R.id.card_add_address);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        cardAddAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddAddressActivity.class);
            startActivityForResult(intent, 200);
        });
    }

    private void loadAddresses() {
        networkManager.getAddresses(new NetworkManager.ApiCallback<java.util.List<Address>>() {
            @Override
            public void onSuccess(java.util.List<Address> addresses) {
                // Bind default and another address (if available)
                Address defaultAddress = null;
                Address otherAddress = null;
                for (Address a : addresses) {
                    if (a.isDefault()) { defaultAddress = a; } else if (otherAddress == null) { otherAddress = a; }
                }

                if (defaultAddress != null) {
                    Address da = defaultAddress;
                    cardHomeAddress.setOnClickListener(v -> returnSelectedAddress(da));
                } else if (!addresses.isEmpty()) {
                    Address a = addresses.get(0);
                    cardHomeAddress.setOnClickListener(v -> returnSelectedAddress(a));
                }

                if (otherAddress != null) {
                    Address oa = otherAddress;
                    cardOfficeAddress.setOnClickListener(v -> returnSelectedAddress(oa));
                } else if (addresses.size() > 1) {
                    Address a2 = addresses.get(1);
                    cardOfficeAddress.setOnClickListener(v -> returnSelectedAddress(a2));
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddressSelectionActivity.this, "Failed to load addresses: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void returnSelectedAddress(Address a) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selected_address", a.getLabel() + ": " + a.getAddress());
        resultIntent.putExtra("address_id", a.getAddressId());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            // When an address is newly created, we don't get ID in this simple flow;
            // trigger refresh so user can tap it (now persisted with ID) or return label-only as fallback.
            String newAddress = data.getStringExtra("new_address");
            if (newAddress != null) {
                // Fallback: return without ID so OrderSummary still shows it; backend will use default if none selected later
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_address", newAddress);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                loadAddresses();
            }
        }
    }
}