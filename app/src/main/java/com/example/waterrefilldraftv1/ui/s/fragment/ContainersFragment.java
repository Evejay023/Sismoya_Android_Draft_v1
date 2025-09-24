package com.example.waterrefilldraftv1.ui.s.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.activities.CartActivity;
import com.example.waterrefilldraftv1.models.CartItem;
import com.example.waterrefilldraftv1.models.Product;
import com.example.waterrefilldraftv1.utils.CartManager;
import java.util.ArrayList;
import java.util.List;

public class ContainersFragment extends Fragment {

    private ImageView ivCart;
    private Button btnAddRound, btnBuyRound;
    private Button btnAddRectangular, btnBuyRectangular;
    private Button btnAddMini, btnBuyMini;

    private List<Product> products;
    private CartManager cartManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_containers, container, false);

        initViews(view);
        setupProducts();
        setupClickListeners();
        cartManager = CartManager.getInstance();

        return view;
    }

    private void initViews(View view) {
        ivCart = view.findViewById(R.id.iv_cart);
        btnAddRound = view.findViewById(R.id.btn_add_round);
        btnBuyRound = view.findViewById(R.id.btn_buy_round);
        btnAddRectangular = view.findViewById(R.id.btn_add_rectangular);
        btnBuyRectangular = view.findViewById(R.id.btn_buy_rectangular);
        btnAddMini = view.findViewById(R.id.btn_add_mini);
        btnBuyMini = view.findViewById(R.id.btn_buy_mini);
    }

    private void setupProducts() {
        products = new ArrayList<>();

        // Round Gallon
        Product roundGallon = new Product(1, "Round Gallon", "Round Gallon", 50, 30.00, R.drawable.round_gallon);
        products.add(roundGallon);

        // Rectangular Gallon
        Product rectangularGallon = new Product(2, "Rectangular Gallon", "Rectangular Gallon", 50, 30.00, R.drawable.rectangular_gallon);
        products.add(rectangularGallon);

        // Mini Gallon
        Product miniGallon = new Product(3, "Mini Gallon", "Mini Gallon", 25, 15.00, R.drawable.mini_gallon);
        products.add(miniGallon);
    }

    private void setupClickListeners() {
        ivCart.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CartActivity.class);
            startActivity(intent);
        });

        btnAddRound.setOnClickListener(v -> showProductDialog(products.get(0)));
        btnBuyRound.setOnClickListener(v -> buyNow(products.get(0)));

        btnAddRectangular.setOnClickListener(v -> showProductDialog(products.get(1)));
        btnBuyRectangular.setOnClickListener(v -> buyNow(products.get(1)));

        btnAddMini.setOnClickListener(v -> showProductDialog(products.get(2)));
        btnBuyMini.setOnClickListener(v -> buyNow(products.get(2)));
    }

    private void showProductDialog(Product product) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_product_detail);
        dialog.setCancelable(true);

        // Initialize dialog views
        ImageView ivClose = dialog.findViewById(R.id.iv_close);
        ImageView ivProductImage = dialog.findViewById(R.id.iv_product_image);
        TextView tvProductType = dialog.findViewById(R.id.tv_product_type);
        TextView tvProductLiters = dialog.findViewById(R.id.tv_product_liters);
        TextView tvProductPrice = dialog.findViewById(R.id.tv_product_price);
        TextView tvQuantity = dialog.findViewById(R.id.tv_quantity);
        TextView tvTotalAmount = dialog.findViewById(R.id.tv_total_amount);
        ImageButton btnDecrease = dialog.findViewById(R.id.btn_decrease);
        ImageButton btnIncrease = dialog.findViewById(R.id.btn_increase);
        Button btnAddToCart = dialog.findViewById(R.id.btn_add_to_cart);

        // Set product data
        ivProductImage.setImageResource(product.getImageResource());
        tvProductType.setText("Type: " + product.getName());
        tvProductLiters.setText("Liters: " + product.getLiters());
        tvProductPrice.setText("Price: " + String.format("%.2f", product.getPrice()));

        final int[] quantity = {1};
        updateQuantityAndTotal(tvQuantity, tvTotalAmount, quantity[0], product.getPrice());

        // Quantity controls
        btnDecrease.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                updateQuantityAndTotal(tvQuantity, tvTotalAmount, quantity[0], product.getPrice());
            }
        });

        btnIncrease.setOnClickListener(v -> {
            quantity[0]++;
            updateQuantityAndTotal(tvQuantity, tvTotalAmount, quantity[0], product.getPrice());
        });

        // Add to cart
        btnAddToCart.setOnClickListener(v -> {
            CartItem cartItem = new CartItem(product, quantity[0]);
            cartManager.addToCart(cartItem);
            Toast.makeText(getContext(), "Added to cart successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        ivClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void updateQuantityAndTotal(TextView tvQuantity, TextView tvTotalAmount, int quantity, double price) {
        tvQuantity.setText(String.valueOf(quantity));
        double total = quantity * price;
        tvTotalAmount.setText(String.format("%.2f", total));
    }

    private void buyNow(Product product) {
        // Create a single cart item and go directly to checkout
        CartItem cartItem = new CartItem(product, 1);
        cartManager.clearCart();
        cartManager.addToCart(cartItem);

        Intent intent = new Intent(getContext(), CartActivity.class);
        intent.putExtra("direct_checkout", true);
        startActivity(intent);
    }
}