package com.example.waterrefilldraftv1.Customer.UserInterface.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Customer.UserInterface.activities.DashboardActivity;

public class OrderPlacedDialog extends DialogFragment {

    public static OrderPlacedDialog newInstance() {
        return new OrderPlacedDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_dialog_order_placed, container, false);

        Button btnBackToDashboard = view.findViewById(R.id.btn_back_to_dashboard);
        btnBackToDashboard.setOnClickListener(v -> {
            // Close dialog and go back to dashboard
            dismiss();

            // Optional: return user to dashboard
            Intent intent = new Intent(requireContext(), DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }
}
