package com.example.waterrefilldraftv1.Customer.UserInterface.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterrefilldraftv1.Customer.adapter.AddressAdapter;
import com.example.waterrefilldraftv1.Customer.models.Address;
import com.example.waterrefilldraftv1.Global.network.NetworkManager;
import com.example.waterrefilldraftv1.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AddressSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerAddresses;
    private FloatingActionButton fabAdd;
    private NetworkManager networkManager;
    private AddressAdapter adapter;
    private List<Address> addressList = new ArrayList<>();

    private static final int REQUEST_ADD_ADDRESS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_activity_address_selection);

        recyclerAddresses = findViewById(R.id.recycler_addresses);
        fabAdd = findViewById(R.id.fab_add_address);
        networkManager = new NetworkManager(this);

        // ✅ RecyclerView setup
        recyclerAddresses.setHasFixedSize(true);
        recyclerAddresses.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressAdapter(addressList, this::returnSelectedAddress);
        recyclerAddresses.setAdapter(adapter);

        // ➕ Floating button to add address
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddAddressActivity.class);
            startActivityForResult(intent, REQUEST_ADD_ADDRESS);
        });

        // 🔙 Back button handling (optional if using iv_back)
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> onBackPressed());

        // 🔄 Load all addresses
        loadAddresses();
    }

    private void loadAddresses() {
        networkManager.getAddresses(new NetworkManager.ApiCallback<List<Address>>() {
            @Override
            public void onSuccess(List<Address> addresses) {
                addressList.clear();
                addressList.addAll(addresses);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddressSelectionActivity.this, "Failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void returnSelectedAddress(Address address) {
        Intent result = new Intent();
        result.putExtra("selected_address", address.getLabel() + ": " + address.getAddress());
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_ADDRESS && resultCode == RESULT_OK) {
            loadAddresses(); // refresh list
        }
    }
}
