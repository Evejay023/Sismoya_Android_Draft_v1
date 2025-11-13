package com.example.waterrefilldraftv1.Riders.UserInterrface.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterrefilldraftv1.Global.network.ApiResponse;
import com.example.waterrefilldraftv1.Global.network.ApiService;
import com.example.waterrefilldraftv1.Global.network.RetrofitClient;
import com.example.waterrefilldraftv1.Global.network.TokenManager;
import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.models.Rider;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Rider_PersonalInformationActivity extends AppCompatActivity {

    private TextView tvNameValue, tvEmailValue, tvContactValue;
    private ImageView ivEditName, ivEditEmail, ivEditContact, ivBack;
    private Rider currentRider;
    private String token;
    private static final String TAG = "RiderPersonalInfoAct";

    // ✅ Validation Patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s\\-]+$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_activity_personal_information);

        ivBack = findViewById(R.id.iv_back);
        tvNameValue = findViewById(R.id.tv_name_value);
        tvEmailValue = findViewById(R.id.tv_email);
        tvContactValue = findViewById(R.id.tv_contact);
        ivEditName = findViewById(R.id.btn_edit_name);
        ivEditEmail = findViewById(R.id.btn_edit_email);
        ivEditContact = findViewById(R.id.btn_edit_contact);

        token = TokenManager.getToken(this);

        ivBack.setOnClickListener(v -> finish());
        ivEditName.setOnClickListener(v -> showEditNameDialog());
        ivEditEmail.setOnClickListener(v -> showEditEmailDialog());
        ivEditContact.setOnClickListener(v -> showEditContactDialog());

        loadProfile();
    }

    private void loadProfile() {
        String json = getSharedPreferences("RiderPrefs", MODE_PRIVATE)
                .getString("rider", null);
        if (json != null) {
            currentRider = new Gson().fromJson(json, Rider.class);
            if (currentRider != null) {
                updateUI(currentRider);
            }
        }
    }

    private void updateUI(Rider rider) {
        if (rider == null) return;
        String fullName = (rider.getFirstName() + " " + rider.getLastName()).trim();
        tvNameValue.setText(fullName.isEmpty() ? "No Name" : fullName);
        tvEmailValue.setText(rider.getEmail() != null ? rider.getEmail() : "No Email");
        tvContactValue.setText(rider.getContactNo() != null ? rider.getContactNo() : "No Contact");
    }

    // ✅ FIX: Add this method to convert contact number for backend
    private String formatContactForBackend(String contactNo) {
        if (contactNo == null) return null;

        // Remove any non-digit characters first
        String digitsOnly = contactNo.replaceAll("\\D", "");

        // Convert +63 format to 09 format for backend
        if (digitsOnly.startsWith("63") && digitsOnly.length() == 11) {
            return "0" + digitsOnly.substring(2); // +639098312656 → 09098312656
        }

        // If it's already in 09 format, return as is
        if (digitsOnly.startsWith("09") && digitsOnly.length() == 11) {
            return digitsOnly;
        }

        return digitsOnly;
    }

    private void showEditNameDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rider_dialog_update_name);

        EditText etFirst = dialog.findViewById(R.id.et_first_name);
        EditText etLast = dialog.findViewById(R.id.et_last_name);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        ImageView ivClose = dialog.findViewById(R.id.iv_close);

        if (currentRider != null) {
            etFirst.setText(currentRider.getFirstName());
            etLast.setText(currentRider.getLastName());
        }

        ivClose.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String first = etFirst.getText().toString().trim();
            String last = etLast.getText().toString().trim();

            // ✅ Validate names
            if (!NAME_PATTERN.matcher(first).matches()) {
                etFirst.setError("Only letters and spaces allowed");
                return;
            }
            if (!NAME_PATTERN.matcher(last).matches()) {
                etLast.setError("Only letters and spaces allowed");
                return;
            }

            // ✅ FIX: Convert contact number before sending
            String formattedContact = formatContactForBackend(currentRider.getContactNo());
            updateProfilePartial(first, last, currentRider.getEmail(), formattedContact);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showEditEmailDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rider_dialog_update_email);

        EditText etEmail = dialog.findViewById(R.id.et_email);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        ImageView ivClose = dialog.findViewById(R.id.btn_close);

        if (currentRider != null) etEmail.setText(currentRider.getEmail());

        ivClose.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            // ✅ Validate email format
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    || !(email.endsWith("@gmail.com") || email.endsWith("@yahoo.com") || email.endsWith("@outlook.com"))) {
                etEmail.setError("Enter a valid email (e.g. example@gmail.com)");
                return;
            }

            // ✅ FIX: Convert contact number before sending
            String formattedContact = formatContactForBackend(currentRider.getContactNo());
            updateProfilePartial(currentRider.getFirstName(), currentRider.getLastName(), email, formattedContact);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showEditContactDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rider_dialog_update_contact);

        EditText etContact = dialog.findViewById(R.id.et_contact);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        ImageView ivClose = dialog.findViewById(R.id.iv_close);

        // ✅ Remove any input length restriction (just in case XML has maxLength)
        etContact.setFilters(new android.text.InputFilter[]{});

        if (currentRider != null && currentRider.getContactNo() != null) {
            // ✅ Convert existing +63 format to 09 format for display
            String displayContact = currentRider.getContactNo();
            if (displayContact.startsWith("+63")) {
                displayContact = "0" + displayContact.substring(3); // +639098312656 → 09098312656
            }
            etContact.setText(displayContact);
        }

        ivClose.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String contact = etContact.getText().toString().trim().replaceAll("\\s+", "");

            // ✅ Validate 09 format
            if (!contact.startsWith("09") || contact.length() != 11) {
                etContact.setError("Must start with '09' and be exactly 11 digits");
                return;
            }

            // ✅ No conversion needed - already in correct format for backend
            updateProfilePartial(
                    currentRider.getFirstName(),
                    currentRider.getLastName(),
                    currentRider.getEmail(),
                    contact // Already in 09XXXXXXXXX format
            );

            dialog.dismiss();
            Toast.makeText(this, "Contact updated successfully!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void updateProfilePartial(String first, String last, String email, String contact) {
        if (token == null) {
            Toast.makeText(this, "Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ FIX: Contact is now already in correct format (09XXXXXXXXX)
        Map<String, String> body = new HashMap<>();
        body.put("first_name", first);
        body.put("last_name", last);
        body.put("email", email);
        body.put("contact_no", contact); // Already in correct format

        Log.d(TAG, "Sending to backend - First: " + first + ", Last: " + last + ", Email: " + email + ", Contact: " + contact);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<ApiResponse> call = apiService.updateProfilePartial("Bearer " + token, body);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(Rider_PersonalInformationActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                    // ✅ Update local data (convert to +63 format for display)
                    currentRider.setFirstName(first);
                    currentRider.setLastName(last);
                    currentRider.setEmail(email);

                    // Convert to +63 format for better display
                    String displayContact = contact;
                    if (contact.startsWith("09")) {
                        displayContact = "+63" + contact.substring(1); // 09098312656 → +639098312656
                    }
                    currentRider.setContactNo(displayContact);

                    getSharedPreferences("RiderPrefs", MODE_PRIVATE)
                            .edit()
                            .putString("rider", new Gson().toJson(currentRider))
                            .apply();

                    // ✅ Send broadcast so Profile & Dashboard refresh instantly
                    Intent intent = new Intent("com.example.waterrefilldraftv1.RIDER_PROFILE_UPDATED");
                    sendBroadcast(intent);

                    updateUI(currentRider);

                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Update failed!";
                    Toast.makeText(Rider_PersonalInformationActivity.this, msg, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Update failed: " + msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(Rider_PersonalInformationActivity.this, "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}