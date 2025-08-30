package com.example.waterrefilldraftv1;



import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * DashboardActivity - Main user dashboard
 * Displays water containers, user profile, and order management
 * Shows available products in a grid layout with pricing
 */
public class DashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvUserEmail;
    private RecyclerView recyclerViewContainers;
    private ContainerAdapter containerAdapter;
    private List<WaterContainer> containerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Get user data from intent
        String userName = getIntent().getStringExtra("user_name");
        String userEmail = getIntent().getStringExtra("user_email");

        initViews();
        setupUserInfo(userName, userEmail);
        setupContainers();
    }

    /**
     * Initialize UI components
     */
    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvUserEmail = findViewById(R.id.tv_user_email);
        recyclerViewContainers = findViewById(R.id.recycler_view_containers);
    }

    /**
     * Setup user information display
     * @param userName User's full name
     * @param userEmail User's email address
     */
    private void setupUserInfo(String userName, String userEmail) {
        if (userName != null) {
            tvWelcome.setText("Welcome, " + userName + "!");
        }
        if (userEmail != null) {
            tvUserEmail.setText(userEmail);
        }
    }

    /**
     * Setup water containers RecyclerView
     * Creates sample container data and configures adapter
     */
    private void setupContainers() {
        containerList = createSampleContainers();

        // Setup RecyclerView with grid layout
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerViewContainers.setLayoutManager(layoutManager);

        // Create and set adapter
        containerAdapter = new ContainerAdapter(containerList, this::onContainerClick);
        recyclerViewContainers.setAdapter(containerAdapter);
    }

    /**
     * Create sample water container data
     * @return List of WaterContainer objects
     */
    private List<WaterContainer> createSampleContainers() {
        List<WaterContainer> containers = new ArrayList<>();

        containers.add(new WaterContainer(1, "Small Container", "5L", "₱50.00", R.drawable.mini_container, true));
        containers.add(new WaterContainer(2, "Medium Container", "10L", "₱85.00", R.drawable.slim_container, true));
        containers.add(new WaterContainer(3, "Large Container", "20L", "₱150.00", R.drawable.round_container, true));

        return containers;
    }

    /**
     * Handle container item click
     * @param container Selected water container
     */
    private void onContainerClick(WaterContainer container) {
        if (container.isAvailable()) {
            Toast.makeText(this, "Selected: " + container.getName() + " - " + container.getPrice(), Toast.LENGTH_SHORT).show();
            // Here you can navigate to order screen or add to cart
        } else {
            Toast.makeText(this, container.getName() + " is currently out of stock", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Water Container model class
     */
    public static class WaterContainer {
        private int id;
        private String name;
        private String capacity;
        private String price;
        private int imageResourceId;
        private boolean available;

        public WaterContainer(int id, String name, String capacity, String price, int imageResourceId, boolean available) {
            this.id = id;
            this.name = name;
            this.capacity = capacity;
            this.price = price;
            this.imageResourceId = imageResourceId;
            this.available = available;
        }

        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getCapacity() { return capacity; }
        public String getPrice() { return price; }
        public int getImageResourceId() { return imageResourceId; }
        public boolean isAvailable() { return available; }
    }

    /**
     * Handle back button press
     * Show confirmation dialog before logging out
     */
    @Override
    public void onBackPressed() {
        // Create logout confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Navigate back to launch screen
                    Intent intent = new Intent(DashboardActivity.this, LaunchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}