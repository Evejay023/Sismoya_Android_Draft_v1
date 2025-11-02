package com.example.waterrefilldraftv1.Customer.UserInterface.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.waterrefilldraftv1.Global.network.ApiResponse;
import com.example.waterrefilldraftv1.Customer.models.User;
import com.example.waterrefilldraftv1.Global.network.ApiService;
import com.example.waterrefilldraftv1.Global.network.RetrofitClient;
import com.example.waterrefilldraftv1.Login_Customer_and_Riders.LoginActivity;
import com.example.waterrefilldraftv1.R;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final String ARG_USER = "arg_user";

    private TextView tvUserName, tvUserContact, tvUserEmail;
    private LinearLayout llInformation, llOrders, llAddress, llChangePassword, llLogout;

    private SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    public ProfileFragment() { }

    /** Create a new instance of ProfileFragment with a user object **/
    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        if (user != null) {
            Bundle args = new Bundle();
            args.putString(ARG_USER, new Gson().toJson(user));
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.customer_fragment_profile, container, false);

        // Initialize UI elements
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserContact = view.findViewById(R.id.tv_user_contact);
        tvUserEmail = view.findViewById(R.id.tv_user_email);

        llInformation = view.findViewById(R.id.ll_information);
        llOrders = view.findViewById(R.id.ll_orders);
        llAddress = view.findViewById(R.id.ll_address);
        llChangePassword = view.findViewById(R.id.ll_change_password);
        llLogout = view.findViewById(R.id.ll_logout);

        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        // Try loading user info
        boolean loaded = tryLoadUserFromArguments();
        if (!loaded) loaded = tryLoadUserFromPrefs();
        if (!loaded) fetchProfileFromApi();

        setupClickListeners();
        return view;
    }

    /** Try to load user info from fragment arguments **/
    private boolean tryLoadUserFromArguments() {
        if (getArguments() != null && getArguments().containsKey(ARG_USER)) {
            String userJson = getArguments().getString(ARG_USER);
            if (userJson != null) {
                try {
                    User user = gson.fromJson(userJson, User.class);
                    if (user != null) {
                        updateUi(user);
                        saveUserToPrefs(user);
                        Log.d(TAG, "Loaded user from fragment arguments");
                        return true;
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Failed to parse user from arguments: " + e.getMessage());
                }
            }
        }
        return false;
    }

    /** Try to load user info from SharedPreferences **/
    private boolean tryLoadUserFromPrefs() {
        String userJson = sharedPreferences.getString("user", null);
        if (userJson != null) {
            try {
                User user = gson.fromJson(userJson, User.class);
                if (user != null) {
                    updateUi(user);
                    Log.d(TAG, "Loaded user from SharedPreferences");
                    return true;
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed to parse user from prefs: " + e.getMessage());
            }
        }
        return false;
    }

    /** Fetch user info from API using /profile **/
    private void fetchProfileFromApi() {
        String token = sharedPreferences.getString("token", null);
        if (token == null) {
            Toast.makeText(requireContext(), "Please log in again.", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        // ✅ FIXED — Correct endpoint for fetching user profile
        Call<ApiResponse> call = apiService.getProfile("Bearer " + token);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (!isAdded()) return; // ✅ Prevent crash if fragment detached

                Log.d(TAG, "Profile API response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (!apiResponse.isError() && apiResponse.getUser() != null) {
                        User user = apiResponse.getUser();
                        updateUi(user);
                        saveUserToPrefs(user);
                    } else {
                        showFallbackData();
                    }
                } else {
                    showFallbackData();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Log.e(TAG, "API Error: " + t.getMessage());
                showFallbackData();
            }
        });
    }

    /** Show fallback or placeholder data **/
    private void showFallbackData() {
        boolean loaded = tryLoadUserFromPrefs();
        if (!loaded) {
            updateUiWithPlaceholder();
            Toast.makeText(requireContext(), "Unable to load profile data", Toast.LENGTH_SHORT).show();
        }
    }

    /** Display placeholder values **/
    private void updateUiWithPlaceholder() {
        tvUserName.setText("User Name");
        tvUserEmail.setText("user@example.com");
        tvUserContact.setText("+639123456789");
        Log.d(TAG, "Displaying placeholder data");
    }

    /** Update UI with fetched user data **/
    private void updateUi(User user) {
        String firstName = user.getFirstName() != null ? user.getFirstName().trim() : "";
        String lastName = user.getLastName() != null ? user.getLastName().trim() : "";
        String fullName = (firstName + " " + lastName).trim();
        if (fullName.isEmpty()) fullName = "User Name";

        tvUserName.setText(fullName);
        tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "No email");
        tvUserContact.setText(user.getContactNo() != null ? user.getContactNo() : "No contact");

        Log.d(TAG, "UI updated with user data: " + fullName + ", " + user.getEmail());
    }

    /** Save user data to SharedPreferences **/
    private void saveUserToPrefs(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", gson.toJson(user));
        editor.apply();
    }

    /** Set click listeners for buttons **/
    private void setupClickListeners() {
        llInformation.setOnClickListener(v -> openPersonalInformation());
        llOrders.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Orders screen coming soon!", Toast.LENGTH_SHORT).show());
        llAddress.setOnClickListener(v -> openAddressSelection());
        llChangePassword.setOnClickListener(v -> openChangePassword());
        llLogout.setOnClickListener(v -> logoutUser());
    }

    private void openPersonalInformation() {
        try {
            Intent intent = new Intent(requireContext(),
                    com.example.waterrefilldraftv1.Customer.UserInterface.activities.PersonalInformationActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Personal Information screen unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAddressSelection() {
        try {
            Intent intent = new Intent(requireContext(),
                    com.example.waterrefilldraftv1.Customer.UserInterface.activities.AddressSelectionActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Address screen unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void openChangePassword() {
        try {
            Intent intent = new Intent(requireContext(),
                    com.example.waterrefilldraftv1.Customer.UserInterface.activities.ChangePasswordActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Change Password screen unavailable", Toast.LENGTH_SHORT).show();
        }
    }


    /** Logout the user and clear preferences **/
    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(requireContext(), "You have been logged out", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    /** Redirect to Login screen **/
    private void redirectToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
