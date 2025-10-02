package com.example.waterrefilldraftv1.ui.s.dialog;

import android.app.Dialog;
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
import com.example.waterrefilldraftv1.models.WaterContainer;

public class AddToCartDialog extends DialogFragment {

    private WaterContainer container;
    private ImageView ivProductImage, ivClose;
    private TextView tvProductType, tvProductLiters, tvProductPrice, tvQuantity, tvTotalAmount;
    private ImageButton btnDecrease, btnIncrease;
    private Button btnAddToCart;

    private int quantity = 1;
    private double price = 0.0;

    public static AddToCartDialog newInstance(WaterContainer container) {
        AddToCartDialog dialog = new AddToCartDialog();
        Bundle args = new Bundle();
        args.putString("container_name", container.getName());
        args.putString("container_liters", container.getLiters());
        args.putString("container_price", container.getPrice());
        args.putInt("container_image", container.getImageResourceId());
        args.putBoolean("container_available", container.isAvailable());
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_product_detail, container, false);

        initViews(view);
        setupContainerData();
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
        btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
    }

    private void setupContainerData() {
        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString("container_name", "");
            String liters = args.getString("container_liters", "");
            String priceStr = args.getString("container_price", "0");

            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                price = 0.0;
            }

            tvProductType.setText("Type: " + name);
            tvProductLiters.setText("Liters: " + liters);
            tvProductPrice.setText("Price: " + priceStr);

            ivProductImage.setImageResource(args.getInt("container_image", R.drawable.img_round_container));
            tvQuantity.setText(String.valueOf(quantity));
            updateTotal();
        }
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

        btnAddToCart.setOnClickListener(v -> {
            // Pass data forward (e.g. to OrderSummaryDialog)
            Bundle args = getArguments();
            if (args != null) {
                WaterContainer container = new WaterContainer(
                        args.getString("container_name", ""),
                        args.getString("container_liters", ""),
                        args.getString("container_price", ""),
                        args.getInt("container_image"),
                        args.getBoolean("container_available", true)
                );

                OrderSummaryDialog orderDialog = OrderSummaryDialog.newInstance(container, quantity);
                orderDialog.show(getParentFragmentManager(), "order_summary");
                dismiss();
            }
        });
    }

    private void updateTotal() {
        double total = quantity * price;
        tvTotalAmount.setText(String.format("%.2f", total));
    }
}
