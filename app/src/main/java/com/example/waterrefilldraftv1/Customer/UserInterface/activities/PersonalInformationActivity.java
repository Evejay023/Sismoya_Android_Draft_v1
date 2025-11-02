package com.example.waterrefilldraftv1.Customer.UserInterface.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterrefilldraftv1.Global.network.ApiResponse;
import com.example.waterrefilldraftv1.Customer.models.User;
import com.example.waterrefilldraftv1.Global.network.ApiService;
import com.example.waterrefilldraftv1.Global.network.RetrofitClient;
import com.example.waterrefilldraftv1.Global.network.TokenManager;
import com.example.waterrefilldraftv1.R;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInformationActivity extends AppCompatActivity {

    private TextView tvNameValue, tvEmailValue, tvContactValue;
    private ImageView ivEditName, ivEditEmail, ivEditContact, ivBack;
    private String token;
    private User currentUser;
    private static final String TAG = "PersonalInformationAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_activity_personal_information_infos);

        // Initialize Views
        ivBack = findViewById(R.id.iv_back);
        tvNameValue = findViewById(R.id.tv_name_value);
        tvEmailValue = findViewById(R.id.tv_email_value);
        tvContactValue = findViewById(R.id.tv_contact_value);
        ivEditName = findViewById(R.id.iv_edit_name);
        ivEditEmail = findViewById(R.id.iv_edit_email);
        ivEditContact = findViewById(R.id.iv_edit_contact);

        token = TokenManager.getToken(this);

        // Buttons
        ivBack.setOnClickListener(v -> finish());
        ivEditName.setOnClickListener(v -> showEditNameDialog());
        ivEditEmail.setOnClickListener(v -> showEditEmailDialog());
        ivEditContact.setOnClickListener(v -> showEditContactDialog());

        loadProfile();
    }

    /** 🔹 Load the profile from API */
    private void loadProfile() {
        if (token == null) {
            Toast.makeText(this, "Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<ApiResponse> call = apiService.getProfile("Bearer " + token);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentUser = response.body().getUser();
                    updateUI(currentUser);
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Failed to load profile.";
                    Toast.makeText(PersonalInformationActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(PersonalInformationActivity.this, "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** 🔹 Update the screen with current user data */
    private void updateUI(User user) {
        if (user == null) return;
        String fullName = (user.getFirstName() + " " + user.getLastName()).trim();
        tvNameValue.setText(fullName.isEmpty() ? "No Name" : fullName);
        tvEmailValue.setText(user.getEmail() != null ? user.getEmail() : "No Email");
        tvContactValue.setText(user.getContactNo() != null ? user.getContactNo() : "No Contact");
    }

    // ======================================================
    // 🔹 EDIT DIALOGS
    // ======================================================

    private void showEditNameDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customer_dialog_update_name);

        EditText etFirst = dialog.findViewById(R.id.et_first_name);
        EditText etLast = dialog.findViewById(R.id.et_last_name);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        ImageView ivClose = dialog.findViewById(R.id.iv_close);

        if (currentUser != null) {
            etFirst.setText(currentUser.getFirstName());
            etLast.setText(currentUser.getLastName());
        }

        ivClose.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String first = etFirst.getText().toString().trim();
            String last = etLast.getText().toString().trim();
            updateProfilePartial(first, last, currentUser.getEmail(), currentUser.getContactNo());
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showEditEmailDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customer_dialog_update_email);

        EditText etEmail = dialog.findViewById(R.id.et_email);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        ImageView ivClose = dialog.findViewById(R.id.iv_close);

        if (currentUser != null) etEmail.setText(currentUser.getEmail());

        ivClose.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String newEmail = etEmail.getText().toString().trim();
            updateProfilePartial(currentUser.getFirstName(), currentUser.getLastName(), newEmail, currentUser.getContactNo());
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showEditContactDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customer_dialog_update_contact);

        EditText etContact = dialog.findViewById(R.id.et_contact);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        ImageView ivClose = dialog.findViewById(R.id.iv_close);

        if (currentUser != null) etContact.setText(currentUser.getContactNo());

        ivClose.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String newContact = etContact.getText().toString().trim();
            updateProfilePartial(currentUser.getFirstName(), currentUser.getLastName(), currentUser.getEmail(), newContact);
            dialog.dismiss();
        });

        dialog.show();
    }

    // ======================================================
    // 🔹 API UPDATE CALL (Partial Update using Map)
    // ======================================================

    private void updateProfilePartial(String first, String last, String email, String contact) {
        if (token == null) {
            Toast.makeText(this, "Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("first_name", first);
        body.put("last_name", last);
        body.put("email", email);
        body.put("contact_no", contact); // ✅ Corrected key to match backend

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<ApiResponse> call = apiService.updateProfilePartial("Bearer " + token, body);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    Toast.makeText(PersonalInformationActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    loadProfile(); // ✅ Refresh profile after update
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Update failed!";
                    Toast.makeText(PersonalInformationActivity.this, msg, Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Update failed: " + msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(PersonalInformationActivity.this, "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
