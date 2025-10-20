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

import com.example.waterrefilldraftv1.Customer.models.ApiResponse;
import com.example.waterrefilldraftv1.Customer.models.User;
import com.example.waterrefilldraftv1.Customer.network.ApiService;
import com.example.waterrefilldraftv1.Customer.network.RetrofitClient;
import com.example.waterrefilldraftv1.Customer.UserInterface.activities.LoginActivity;
import com.example.waterrefilldraftv1.R;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final String ARG_USER = "arg_user";

    private TextView tvUserName, tvUserContact, tvUserEmail;
    private LinearLayout llOrders, llAddress, llChangePassword, llLogout;
    private View ibEditName, ibEditEmail, ibEditContact;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

    public ProfileFragment() { }

    /**
     * If you need to create programmatically and pass a User from Dashboard:
     * ProfileFragment frag = new ProfileFragment();
     * Bundle args = new Bundle();
     * args.putString("arg_user", new Gson().toJson(user));
     * frag.setArguments(args);
     */
    
    /**
     * Static method to create ProfileFragment with user data
     */
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

        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserContact = view.findViewById(R.id.tv_user_contact);
        tvUserEmail = view.findViewById(R.id.tv_user_email);

        llOrders = view.findViewById(R.id.ll_orders);
        llAddress = view.findViewById(R.id.ll_address);
        llChangePassword = view.findViewById(R.id.ll_change_password);
        llLogout = view.findViewById(R.id.ll_logout);

        ibEditName = view.findViewById(R.id.ib_edit_name);
        ibEditEmail = view.findViewById(R.id.ib_edit_email);
        ibEditContact = view.findViewById(R.id.ib_edit_contact);

        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        // 1) FIRST: try to read user passed from Dashboard (fragment arguments)
        boolean loaded = tryLoadUserFromArguments();

        // 2) SECOND: if not loaded from args, try SharedPreferences (Profile previously saved)
        if (!loaded) {
            loaded = tryLoadUserFromPrefs();
        }

        // 3) THIRD: if still not loaded, fetch from API (same as before)
        if (!loaded) {
            fetchProfileFromApi();
        } else {
            Log.d(TAG, "Profile loaded successfully from cache/preferences");
        }

        setupClickListeners();

        return view;
    }

    /**
     * Try to load user from fragment arguments (Dashboard -> Profile)
     * @return true if user found and UI updated
     */
    private boolean tryLoadUserFromArguments() {
        if (getArguments() != null && getArguments().containsKey(ARG_USER)) {
            String userJson = getArguments().getString(ARG_USER);
            if (userJson != null) {
                try {
                    User user = gson.fromJson(userJson, User.class);
                    if (user != null) {
                        updateUi(user);
                        // Save to prefs so other fragments/activities can reuse
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

    /**
     * Try to load user from SharedPreferences
     * @return true if user found and UI updated
     */
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

    private void fetchProfileFromApi() {
        String token = sharedPreferences.getString("token", null);
        if (token == null) {
            Toast.makeText(requireContext(), "Please log in again.", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<ApiResponse> call = apiService.getProfile("Bearer " + token);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                Log.d(TAG, "Profile API response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Log.d(TAG, "Profile API response: " + new Gson().toJson(apiResponse));
                    
                    if (!apiResponse.isError()) {
                        User user = apiResponse.getUser();
                        if (user != null) {
                            Log.d(TAG, "Profile API success - User loaded: " + user.getFirstName() + " " + user.getLastName());
                            updateUi(user);
                            saveUserToPrefs(user);
                        } else {
                            Log.w(TAG, "Profile API returned no user object");
                            showFallbackData();
                        }
                    } else {
                        Log.w(TAG, "Profile API returned error: " + apiResponse.getMessage());
                        showFallbackData();
                    }
                } else {
                    Log.w(TAG, "Profile API failed with code: " + response.code());
                    showFallbackData();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API Error: " + t.getMessage());
                showFallbackData();
            }
        });
    }

    private void showFallbackData() {
        boolean loaded = tryLoadUserFromPrefs();
        if (!loaded) {
            // Show placeholder data if no user data is available
            updateUiWithPlaceholder();
            Toast.makeText(requireContext(), "Unable to load profile data", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUiWithPlaceholder() {
        tvUserName.setText("User Name");
        tvUserEmail.setText("user@example.com");
        tvUserContact.setText("+639123456789");
        Log.d(TAG, "Displaying placeholder data");
    }

    private void updateUi(User user) {
        if (user == null) {
            Log.w(TAG, "User object is null, cannot update UI");
            return;
        }
        
        // Safely build full name
        String firstName = user.getFirstName() != null ? user.getFirstName().trim() : "";
        String lastName = user.getLastName() != null ? user.getLastName().trim() : "";
        String fullName = (firstName + " " + lastName).trim();
        
        if (fullName.isEmpty()) {
            fullName = "User Name";
        }
        
        tvUserName.setText(fullName);
        tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "No email");
        tvUserContact.setText(user.getContactNo() != null ? user.getContactNo() : "No contact");
        
        Log.d(TAG, "UI updated with user data: " + fullName + ", " + user.getEmail());
    }

    private void saveUserToPrefs(User user) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user", gson.toJson(user));
            editor.apply();
            Log.d(TAG, "Saved user to SharedPreferences");
        } catch (Exception e) {
            Log.w(TAG, "Failed to save user to prefs: " + e.getMessage());
        }
    }

    private void setupClickListeners() {
        llOrders.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Orders screen coming soon!", Toast.LENGTH_SHORT).show());

        llAddress.setOnClickListener(v -> openAddressSelection());

        llChangePassword.setOnClickListener(v -> openChangePassword());

        llLogout.setOnClickListener(v -> logoutUser());

        if (ibEditName != null) ibEditName.setOnClickListener(v -> openEditNameDialog());
        if (ibEditEmail != null) ibEditEmail.setOnClickListener(v -> openEditEmailDialog());
        if (ibEditContact != null) ibEditContact.setOnClickListener(v -> openEditContactDialog());
    }

    private void openAddressSelection() {
        try {
            Intent intent = new Intent(requireContext(), com.example.waterrefilldraftv1.Customer.UserInterface.activities.AddressSelectionActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Address screen unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void openChangePassword() {
        try {
            Intent intent = new Intent(requireContext(), com.example.waterrefilldraftv1.Customer.UserInterface.activities.ChangePasswordActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Change Password screen unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void openEditNameDialog() {
        showSimpleDialog(R.layout.customer_dialog_update_name);
    }

    private void openEditEmailDialog() {
        showSimpleDialog(R.layout.customer_dialog_update_email);
    }

    private void openEditContactDialog() {
        showSimpleDialog(R.layout.customer_dialog_update_contact);
    }

    private void showSimpleDialog(int layoutRes) {
        android.app.Dialog dialog = new android.app.Dialog(requireContext());
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(layoutRes);
        dialog.setCancelable(true);
        android.view.View btnClose = dialog.findViewById(R.id.btn_close);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }
        dialog.show();
    }

    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(requireContext(), "You have been logged out", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
