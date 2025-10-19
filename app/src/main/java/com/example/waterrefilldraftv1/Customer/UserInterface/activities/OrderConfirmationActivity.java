package com.example.waterrefilldraftv1.Customer.UserInterface.activities;


import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.waterrefilldraftv1.R;

public class OrderConfirmationActivity extends AppCompatActivity {

    private ImageView ivClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        initViews();
        setupClickListeners();

        // Auto close after 3 seconds
        new Handler().postDelayed(() -> {
            finish();
        }, 3000);
    }

    private void initViews() {
        ivClose = findViewById(R.id.iv_close);
    }

    private void setupClickListeners() {
        ivClose.setOnClickListener(v -> finish());
    }

    @Override
    public void onBackPressed() {
        // Override back press to close the activity
        finish();
    }
}