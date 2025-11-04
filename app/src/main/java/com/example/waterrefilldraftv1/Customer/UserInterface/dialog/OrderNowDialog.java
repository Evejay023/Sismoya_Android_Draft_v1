package com.example.waterrefilldraftv1.Customer.UserInterface.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Customer.UserInterface.activities.OrderSummaryActivity;
import com.example.waterrefilldraftv1.Customer.models.WaterContainer;

public class OrderNowDialog extends DialogFragment {

    private ImageView ivProductImage, ivClose;
    private TextView tvProductType, tvProductLiters, tvProductPrice, tvQuantity, tvTotalAmount;
    private ImageButton btnDecrease, btnIncrease;
    private Button btnOrderNow;

    private int quantity = 1;
    private double price = 0.0;
    private String productName;
    private String productLiters;
    private int productImage;
    private int productId;

    public static OrderNowDialog newInstance(WaterContainer container, int backendGallonId) {
        OrderNowDialog dialog = new OrderNowDialog();
        Bundle args = new Bundle();
        args.putString("container_name", container.getName());
        args.putString("container_liters", Integer.toString(container.getLiters()));
        args.putString("container_price", container.getPrice());
        args.putInt("container_image", container.getImageResourceId());
        args.putInt("gallon_id", backendGallonId);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_dialog_order_now, container, false);
        initViews(view);
        setupDataFromArgs();
        setupClickListeners();
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        return dialog;
    }

    private void initViews(View view) {
        ivClose = view.findViewById(R.id.iv_close);
        ivProductImage = view.findViewById(R.id.iv_product_image);
        tvProductType = view.findViewById(R.id.tv_product_type);
        tvProductLiters = view.findViewById(R.id.tv_product_liters);
        tvProductPrice = view.findViewById(R.id.tv_product_price);
        tvQuantity = view.findViewById(R.id.tv_quantity);
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        btnDecrease = view.findViewById(R.id.btn_decrease);
        btnIncrease = view.findViewById(R.id.btn_increase);
        btnOrderNow = view.findViewById(R.id.btn_order_now);
    }

    private void setupDataFromArgs() {
        Bundle args = getArguments();
        if (args == null) return;
        productName = args.getString("container_name", "");
        productLiters = args.getString("container_liters", "");
        String priceStr = args.getString("container_price", "0");
        productImage = args.getInt("container_image", R.drawable.img_round_container);
        productId = args.getInt("gallon_id", 0);

        try { price = Double.parseDouble(priceStr); } catch (Exception ignored) { price = 0.0; }

        tvProductType.setText("Type: " + productName);
        tvProductLiters.setText("Liters: " + productLiters + " liters");
        tvProductPrice.setText("Price: " + priceStr);
        ivProductImage.setImageResource(productImage);
        tvQuantity.setText(String.valueOf(quantity));
        updateTotal();
    }

    private void setupClickListeners() {
        ivClose.setOnClickListener(v -> dismiss());

        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updateTotal();
            }
        });

        btnIncrease.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
            updateTotal();
        });

        btnOrderNow.setOnClickListener(v -> goToSummary());
    }

    private void goToSummary() {
        if (getContext() == null) return;
        Intent intent = new Intent(getContext(), OrderSummaryActivity.class);
        intent.putExtra("buy_now", true);
        intent.putExtra("gallon_id", productId);
        intent.putExtra("name", productName);
        intent.putExtra("liters", productLiters);
        intent.putExtra("price", price);
        intent.putExtra("image_res", productImage);
        intent.putExtra("quantity", quantity);
        startActivity(intent);
        dismiss();
    }

    private void updateTotal() {
        double total = quantity * price;
        tvTotalAmount.setText(String.format("%.2f", total));
    }
}


