package com.example.waterrefilldraftv1;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class PaymentMethodDialog extends DialogFragment {

    private TextView tvTotalAmount;
    private Button btnPay;

    public static PaymentMethodDialog newInstance(String totalAmount) {
        PaymentMethodDialog dialog = new PaymentMethodDialog();
        Bundle args = new Bundle();
        args.putString("total_amount", totalAmount);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_payment_method, container, false);

        initViews(view);
        setupPaymentData();
        setupClickListeners();

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    private void initViews(View view) {
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        btnPay = view.findViewById(R.id.btn_pay);
    }

    private void setupPaymentData() {
        Bundle args = getArguments();
        if (args != null) {
            tvTotalAmount.setText("Total Amount: " + args.getString("total_amount", "30"));
        }
    }

    private void setupClickListeners() {
        btnPay.setOnClickListener(v -> {
            // Show order placed dialog
            OrderPlacedDialog orderPlacedDialog = new OrderPlacedDialog();
            orderPlacedDialog.show(getParentFragmentManager(), "order_placed");
            dismiss();
        });
    }
}