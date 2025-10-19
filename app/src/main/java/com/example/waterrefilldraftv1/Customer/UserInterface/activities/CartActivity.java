package com.example.waterrefilldraftv1.Customer.UserInterface.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Customer.models.CartItem;
import com.example.waterrefilldraftv1.Customer.models.WaterContainer;
import com.example.waterrefilldraftv1.Customer.models.ServerCartItem;
import com.example.waterrefilldraftv1.Customer.network.ApiService;
import com.example.waterrefilldraftv1.Customer.network.RetrofitClient;
import com.example.waterrefilldraftv1.Customer.UserInterface.dialog.OrderSummaryDialog;
import com.example.waterrefilldraftv1.Customer.utils.CartManager;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private ImageView ivBack;
    private LinearLayout llCartItems;
    private Button btnCheckout;
    private TextView tvTotalAmountFooter;
    private TextView tvItemsSelected;
    private CartManager cartManager;
    private boolean directCheckout = false;

    // Server sync state
    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private final Map<Integer, Integer> gallonIdToCartItemId = new HashMap<>(); // gallon_id -> cart_item_id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = CartManager.getInstance();
        directCheckout = getIntent().getBooleanExtra("direct_checkout", false);

        initViews();
        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        setupClickListeners();
        fetchCartFromServer();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        llCartItems = findViewById(R.id.ll_cart_items);
        btnCheckout = findViewById(R.id.btn_checkout);
        tvTotalAmountFooter = findViewById(R.id.tv_total_amount_footer);
        tvItemsSelected = findViewById(R.id.tv_items_selected);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        btnCheckout.setOnClickListener(v -> {
            if (!cartManager.isEmpty()) {
                double totalAmount = 0;
                CartItem firstItem = null;

                for (CartItem item : cartManager.getCartItems()) {
                    if (item.isSelected()) {
                        totalAmount += item.getTotalPrice();
                        if (firstItem == null) firstItem = item;
                    }
                }

                if (firstItem != null) {
                    WaterContainer container = new WaterContainer(
                            firstItem.getProduct().getType(),
                            String.format("%.2f", firstItem.getProduct().getPrice()),
                            firstItem.getProduct().getImageResource()
                    );

                    int quantity = firstItem.getQuantity();

                    // Show the order summary dialog
                    FragmentManager fm = getSupportFragmentManager();
                    OrderSummaryDialog dialog = OrderSummaryDialog.newInstance(container, quantity);
                    dialog.show(fm, "order_summary");
                }
            }
        });
    }

    private void loadCartItems() {
        llCartItems.removeAllViews();
        List<CartItem> cartItems = cartManager.getCartItems();

        if (cartItems.isEmpty()) {
            showEmptyCartMessage();
            return;
        }

        for (CartItem cartItem : cartItems) {
            int cartItemId = gallonIdToCartItemId.getOrDefault(cartItem.getProduct().getId(), -1);
            View itemView = createCartItemView(cartItem, cartItemId);
            llCartItems.addView(itemView);
        }

        updateFooter();

        if (directCheckout) {
            btnCheckout.performClick();
        }
    }

    private View createCartItemView(CartItem cartItem, int serverCartItemId) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.dialog_item_container_order, null);

        CheckBox cbSelectItem = itemView.findViewById(R.id.cb_select_item);
        ImageView ivProductImage = itemView.findViewById(R.id.iv_product_image);
        TextView tvProductType = itemView.findViewById(R.id.tv_product_type);
        TextView tvProductLiters = itemView.findViewById(R.id.tv_product_liters);
        TextView tvProductPrice = itemView.findViewById(R.id.tv_product_price);
        TextView tvQuantity = itemView.findViewById(R.id.tv_quantity);
        TextView tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
        ImageView btnDecrease = itemView.findViewById(R.id.btn_decrease);
        ImageView btnIncrease = itemView.findViewById(R.id.btn_increase);
        ImageView btnDelete = itemView.findViewById(R.id.btn_delete);

        // Set data from CartItem to match your UI structure
        ivProductImage.setImageResource(cartItem.getProduct().getImageResource());
        tvProductType.setText(cartItem.getProduct().getType()); // "Round Gallon", "Rectangular Gallon", etc.
        tvProductLiters.setText("Liters: " + cartItem.getProduct().getLiters());
        tvProductPrice.setText("Price: " + String.format("%.2f", cartItem.getProduct().getPrice()));
        tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
        tvTotalAmount.setText("Total: ₱" + String.format("%.2f", cartItem.getTotalPrice()));

        // Set checkbox state
        cbSelectItem.setChecked(cartItem.isSelected());

        cbSelectItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartItem.setSelected(isChecked);
            updateFooter();
        });

        btnDecrease.setOnClickListener(v -> {
            if (serverCartItemId <= 0) {
                if (cartItem.getQuantity() > 1) {
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
                    tvTotalAmount.setText("Total: ₱" + String.format("%.2f", cartItem.getTotalPrice()));
                    updateFooter();
                }
                return;
            }
            String token = sharedPreferences.getString("token", null);
            if (token == null) { Toast.makeText(this, "Login required", Toast.LENGTH_SHORT).show(); return; }
            Map<String, Integer> body = new HashMap<>();
            body.put("cart_item_id", serverCartItemId);
            apiService.decreaseCartItem("Bearer " + token, body).enqueue(new Callback<com.example.waterrefilldraftv1.Customer.models.ApiResponse>() {
                @Override public void onResponse(Call<com.example.waterrefilldraftv1.Customer.models.ApiResponse> call, Response<com.example.waterrefilldraftv1.Customer.models.ApiResponse> response) {
                    fetchCartFromServer();
                }
                @Override public void onFailure(Call<com.example.waterrefilldraftv1.Customer.models.ApiResponse> call, Throwable t) {
                    Toast.makeText(CartActivity.this, "Failed to update quantity", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnIncrease.setOnClickListener(v -> {
            if (serverCartItemId <= 0) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
                tvTotalAmount.setText("Total: ₱" + String.format("%.2f", cartItem.getTotalPrice()));
                updateFooter();
                return;
            }
            String token = sharedPreferences.getString("token", null);
            if (token == null) { Toast.makeText(this, "Login required", Toast.LENGTH_SHORT).show(); return; }
            Map<String, Integer> body = new HashMap<>();
            body.put("cart_item_id", serverCartItemId);
            apiService.increaseCartItem("Bearer " + token, body).enqueue(new Callback<com.example.waterrefilldraftv1.Customer.models.ApiResponse>() {
                @Override public void onResponse(Call<com.example.waterrefilldraftv1.Customer.models.ApiResponse> call, Response<com.example.waterrefilldraftv1.Customer.models.ApiResponse> response) {
                    fetchCartFromServer();
                }
                @Override public void onFailure(Call<com.example.waterrefilldraftv1.Customer.models.ApiResponse> call, Throwable t) {
                    Toast.makeText(CartActivity.this, "Failed to update quantity", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnDelete.setOnClickListener(v -> {
            if (serverCartItemId <= 0) {
                cartManager.removeItem(cartItem.getProduct());
                loadCartItems();
                return;
            }
            String token = sharedPreferences.getString("token", null);
            if (token == null) { Toast.makeText(this, "Login required", Toast.LENGTH_SHORT).show(); return; }
            Map<String, java.util.List<Integer>> body = new HashMap<>();
            java.util.List<Integer> ids = new ArrayList<>();
            ids.add(serverCartItemId);
            body.put("cart_item_ids", ids);
            apiService.removeFromCart("Bearer " + token, body).enqueue(new Callback<com.example.waterrefilldraftv1.Customer.models.ApiResponse>() {
                @Override public void onResponse(Call<com.example.waterrefilldraftv1.Customer.models.ApiResponse> call, Response<com.example.waterrefilldraftv1.Customer.models.ApiResponse> response) {
                    fetchCartFromServer();
                }
                @Override public void onFailure(Call<com.example.waterrefilldraftv1.Customer.models.ApiResponse> call, Throwable t) {
                    Toast.makeText(CartActivity.this, "Failed to remove item", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return itemView;
    }

    private void showEmptyCartMessage() {
        TextView emptyMessage = new TextView(this);
        emptyMessage.setText("Your cart is empty");
        emptyMessage.setTextSize(16);
        emptyMessage.setTextColor(getResources().getColor(R.color.text_light));
        emptyMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        emptyMessage.setPadding(0, 100, 0, 100);
        llCartItems.addView(emptyMessage);

        btnCheckout.setEnabled(false);
        btnCheckout.setText("Cart is Empty");
        tvTotalAmountFooter.setText("₱0.00");
        tvItemsSelected.setText("0 items selected");
    }

    private void updateFooter() {
        double totalPrice = 0;
        int totalItems = 0;
        int selectedCount = 0;

        for (CartItem item : cartManager.getCartItems()) {
            if (item.isSelected()) {
                totalPrice += item.getTotalPrice();
                totalItems += item.getQuantity();
                selectedCount++;
            }
        }

        tvTotalAmountFooter.setText("₱" + String.format("%.2f", totalPrice));
        tvItemsSelected.setText(selectedCount + " items selected");

        btnCheckout.setEnabled(selectedCount > 0);

        if (selectedCount > 0) {
            btnCheckout.setText("Checkout");
        } else {
            btnCheckout.setText("Select Items");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchCartFromServer();
    }

    private void fetchCartFromServer() {
        String token = sharedPreferences.getString("token", null);
        if (token == null) { loadCartItems(); return; }
        apiService.getCartItems("Bearer " + token).enqueue(new Callback<List<ServerCartItem>>() {
            @Override public void onResponse(Call<List<ServerCartItem>> call, Response<List<ServerCartItem>> response) {
                if (response.isSuccessful() && response.body()!=null) {
                    gallonIdToCartItemId.clear();
                    cartManager.clearCart();
                    for (ServerCartItem s : response.body()) {
                        // map server item to local UI item
                        int liters = 0;
                        try { if (s.size != null) { String digits = s.size.replaceAll("[^0-9]", ""); liters = digits.isEmpty()?0:Integer.parseInt(digits); } } catch (Exception ignored) {}
                        com.example.waterrefilldraftv1.Customer.models.Product p = new com.example.waterrefilldraftv1.Customer.models.Product(
                                s.gallonId,
                                s.name != null ? s.name : "Gallon",
                                s.name != null ? s.name : "Gallon",
                                liters,
                                s.price,
                                com.example.waterrefilldraftv1.R.drawable.img_round_container
                        );
                        CartItem ci = new CartItem(p, s.quantity);
                        cartManager.addToCart(ci);
                        gallonIdToCartItemId.put(s.gallonId, s.cartItemId);
                    }
                    loadCartItems();
                } else {
                    loadCartItems();
                }
            }
            @Override public void onFailure(Call<List<ServerCartItem>> call, Throwable t) {
                loadCartItems();
            }
        });
    }
}