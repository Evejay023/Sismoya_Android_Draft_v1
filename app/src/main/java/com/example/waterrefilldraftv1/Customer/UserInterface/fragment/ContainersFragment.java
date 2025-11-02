package com.example.waterrefilldraftv1.Customer.UserInterface.fragment;

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
import com.example.waterrefilldraftv1.Customer.UserInterface.activities.CartActivity;
import com.example.waterrefilldraftv1.Customer.models.CartItem;
import com.example.waterrefilldraftv1.Customer.models.Product;
import com.example.waterrefilldraftv1.Customer.models.ProductDto;
import com.example.waterrefilldraftv1.Global.network.NetworkManager;
import com.example.waterrefilldraftv1.Customer.utils.CartManager;
import com.example.waterrefilldraftv1.Customer.UserInterface.dialog.OrderNowDialog;
import java.util.ArrayList;
import java.util.List;

public class ContainersFragment extends Fragment {

    private ImageView ivCart;
    private Button btnAddRound, btnBuyRound;
    private Button btnAddRectangular, btnBuyRectangular;
    private Button btnAddMini, btnBuyMini;

    private List<Product> products = new ArrayList<>();
    private CartManager cartManager;
    private NetworkManager networkManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_fragment_containers, container, false);

        initViews(view);
        cartManager = CartManager.getInstance();
        networkManager = new NetworkManager(requireContext());

        setupProducts();
        setupClickListeners();

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
        // Load from backend
        networkManager.fetchGallons(new NetworkManager.ApiCallback<List<ProductDto>>() {
            @Override
            public void onSuccess(List<ProductDto> response) {
                products.clear();
                for (ProductDto dto : response) {
                    products.add(mapDto(dto));
                }
                ensureThreeItemsFallback();
            }

            @Override
            public void onError(String error) {
                ensureThreeItemsFallback();
            }
        });
    }

    private void ensureThreeItemsFallback() {
        if (products.isEmpty()) {
            products.add(new Product(2, "Round Gallon", "Round Gallon", 50, 30.00, R.drawable.img_round_container));
            products.add(new Product(1, "Slim Gallon", "Slim Gallon", 50, 30.00, R.drawable.img_slim_container));
            products.add(new Product(3, "Mini Gallon", "Mini Gallon", 25, 15.00, R.drawable.img_mini_container));
        } else {
            // Guarantee at least 3
            while (products.size() < 3) {
                products.add(new Product(100 + products.size(), "Gallon", "Gallon", 50, 30.00, R.drawable.img_round_container));
            }
        }
    }

    private Product mapDto(ProductDto dto) {
        int liters = 0;
        try { liters = Integer.parseInt(dto.getCapacity()); } catch (Exception ignored) {}
        int imageRes = R.drawable.img_round_container;
        String name = dto.getName() == null ? "Gallon" : dto.getName();
        String lower = name.toLowerCase();
        if (lower.contains("mini")) imageRes = R.drawable.img_mini_container;
        else if (lower.contains("slim") || lower.contains("rect")) imageRes = R.drawable.img_slim_container;
        else if (lower.contains("round")) imageRes = R.drawable.img_round_container;
        return new Product(dto.getId(), name, name, liters, dto.getPrice(), imageRes);
    }

    private void setupClickListeners() {
        ivCart.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CartActivity.class);
            startActivity(intent);
        });

        setClickByKeyword(btnAddRound, "round", true);
        setBuyNowByKeyword(btnBuyRound, "round");
        setClickByKeyword(btnAddRectangular, "slim", true);
        setBuyNowByKeyword(btnBuyRectangular, "slim");
        setClickByKeyword(btnAddMini, "mini", true);
        setBuyNowByKeyword(btnBuyMini, "mini");
    }

    private void setClickByKeyword(Button btn, String keyword, boolean isAdd) {
        if (btn == null) return;
        btn.setOnClickListener(v -> {
            Product product = findProductByKeyword(keyword);
            if (product != null) {
                if (isAdd) showProductDialog(product);
                else buyNow(product);
            } else {
                Toast.makeText(getContext(), "Product not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Product findProductByKeyword(String keyword) {
        String k = keyword.toLowerCase();
        for (Product p : products) {
            String n = p.getName() != null ? p.getName().toLowerCase() : "";
            if (n.contains(k)) return p;
        }
        return null;
    }

    private void showProductDialog(Product product) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customer_dialog_product_detail);
        dialog.setCancelable(true);

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

        if (ivProductImage != null) ivProductImage.setImageResource(product.getImageResource());
        if (tvProductType != null) tvProductType.setText("Type: " + product.getName());
        if (tvProductLiters != null) tvProductLiters.setText("Liters: " + product.getLiters());
        if (tvProductPrice != null) tvProductPrice.setText("Price: " + String.format("%.2f", product.getPrice()));

        final int[] quantity = {1};
        updateQuantityAndTotal(tvQuantity, tvTotalAmount, quantity[0], product.getPrice());

        if (btnDecrease != null) {
            btnDecrease.setOnClickListener(v -> {
                if (quantity[0] > 1) {
                    quantity[0]--;
                    updateQuantityAndTotal(tvQuantity, tvTotalAmount, quantity[0], product.getPrice());
                }
            });
        }

        if (btnIncrease != null) {
            btnIncrease.setOnClickListener(v -> {
                quantity[0]++;
                updateQuantityAndTotal(tvQuantity, tvTotalAmount, quantity[0], product.getPrice());
            });
        }

        if (btnAddToCart != null) {
            btnAddToCart.setOnClickListener(v -> {
                CartItem cartItem = new CartItem(product, quantity[0]);
                cartManager.addToCart(cartItem);
                Toast.makeText(getContext(), "Added to cart successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        }

        if (ivClose != null) {
            ivClose.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }

    private void updateQuantityAndTotal(TextView tvQuantity, TextView tvTotalAmount, int quantity, double price) {
        if (tvQuantity != null) tvQuantity.setText(String.valueOf(quantity));
        double total = quantity * price;
        if (tvTotalAmount != null) tvTotalAmount.setText(String.format("%.2f", total));
    }

    private void buyNow(Product product) {
        // Open dedicated Buy Now dialog that skips cart
        com.example.waterrefilldraftv1.Customer.models.WaterContainer container = new com.example.waterrefilldraftv1.Customer.models.WaterContainer(
                product.getName(),
                String.format("%.2f", product.getPrice()),
                product.getImageResource()
        );
        OrderNowDialog dialog = OrderNowDialog.newInstance(container, product.getId());
        dialog.show(requireActivity().getSupportFragmentManager(), "order_now");
    }

    private void setBuyNowByKeyword(Button btn, String keyword) {
        if (btn == null) return;
        btn.setOnClickListener(v -> {
            Product product = findProductByKeyword(keyword);
            if (product == null) {
                Toast.makeText(getContext(), "Product not loaded yet", Toast.LENGTH_SHORT).show();
                return;
            }
            buyNow(product);
        });
    }
}
