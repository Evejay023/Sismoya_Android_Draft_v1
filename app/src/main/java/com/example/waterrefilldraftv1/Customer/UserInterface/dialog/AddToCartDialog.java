package com.example.waterrefilldraftv1.Customer.UserInterface.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Customer.models.CartItem;
import com.example.waterrefilldraftv1.Customer.models.Product;
import com.example.waterrefilldraftv1.Customer.models.WaterContainer;
import com.example.waterrefilldraftv1.Customer.utils.CartManager;

public class AddToCartDialog extends DialogFragment {

    private ImageView ivProductImage, ivClose;
    private TextView tvProductType, tvProductLiters, tvProductPrice, tvQuantity, tvTotalAmount;
    private ImageButton btnDecrease, btnIncrease;
    private Button btnAddToCart;

    private int quantity = 1;
    private double price = 0.0;
    private String productName;
    private String productLiters;
    private int productImage;
    private CartManager cartManager;

    public static AddToCartDialog newInstance(WaterContainer container) {
        AddToCartDialog dialog = new AddToCartDialog();
        Bundle args = new Bundle();
        args.putString("container_name", container.getName());
        args.putString("container_liters", Integer.toString(container.getLiters()));
        args.putString("container_price", container.getPrice());
        args.putInt("container_image", container.getImageResourceId());
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_dialog_product_detail, container, false);

        cartManager = CartManager.getInstance();
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
            productName = args.getString("container_name", "");
            productLiters = args.getString("container_liters", "");
            String priceStr = args.getString("container_price", "0");
            productImage = args.getInt("container_image", R.drawable.img_round_container);

            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                price = 0.0;
            }

            // Set data to match your UI structure
            tvProductType.setText("Type: " + productName);
            tvProductLiters.setText("Liters: " + productLiters);
            tvProductPrice.setText("Price: " + priceStr);
            ivProductImage.setImageResource(productImage);
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
            addToCart();
        });
    }

    private void addToCart() {
        try {
            // Extract liters from the string (e.g., "50" from "Liters: 50")
            int liters = extractLitersFromString(productLiters);

            // Create a Product object
            Product product = new Product(
                    generateProductId(productName), // Generate ID based on name
                    productName,
                    productName, // Using name as type for simplicity
                    liters,
                    price,
                    productImage
            );

            // Create CartItem
            CartItem cartItem = new CartItem(product, quantity);

            // Add to cart
            cartManager.addToCart(cartItem);

            // Show success message
            Toast.makeText(getContext(), "Added to cart successfully!", Toast.LENGTH_SHORT).show();

            // Close dialog
            dismiss();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to add to cart", Toast.LENGTH_SHORT).show();
        }
    }

    private int extractLitersFromString(String litersString) {
        try {
            // Handle different formats: "Liters: 50", "50", "50L", etc.
            String numericPart = litersString.replaceAll("[^0-9]", "");
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            return 5; // Default value
        }
    }

    private int generateProductId(String productName) {
        // Generate a simple ID based on product name
        switch (productName.toLowerCase()) {
            case "round gallon":
                return 1;
            case "rectangular gallon":
                return 2;
            case "mini gallon":
                return 3;
            default:
                return Math.abs(productName.hashCode());
        }
    }

    private void updateTotal() {
        double total = quantity * price;
        tvTotalAmount.setText(String.format("%.2f", total));
    }
}