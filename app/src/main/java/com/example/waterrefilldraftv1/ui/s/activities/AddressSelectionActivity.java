package com.example.waterrefilldraftv1.ui.s.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.waterrefilldraftv1.R;

public class AddressSelectionActivity extends AppCompatActivity {

    private ImageView ivBack;
    private CardView cardHomeAddress, cardOfficeAddress, cardAddAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_selection);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        cardHomeAddress = findViewById(R.id.card_home_address);
        cardOfficeAddress = findViewById(R.id.card_office_address);
        cardAddAddress = findViewById(R.id.card_add_address);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        cardHomeAddress.setOnClickListener(v -> {
            returnSelectedAddress("1st Saint St, Bagong Bario...");
        });

        cardOfficeAddress.setOnClickListener(v -> {
            returnSelectedAddress("University of Celocan City");
        });

        cardAddAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddAddressActivity.class);
            startActivityForResult(intent, 200);
        });
    }

    private void returnSelectedAddress(String address) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selected_address", address);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            String newAddress = data.getStringExtra("new_address");
            returnSelectedAddress(newAddress);
        }
    }
}