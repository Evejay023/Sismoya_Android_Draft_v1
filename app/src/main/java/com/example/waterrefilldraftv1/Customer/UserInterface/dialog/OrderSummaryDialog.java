package com.example.waterrefilldraftv1.Customer.UserInterface.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Customer.models.WaterContainer;

public class OrderSummaryDialog extends DialogFragment {

    private ImageView ivContainer, btnBack, btnEditAddress;
    private TextView tvCustomerName, tvContactNo, tvAddress, tvPickupTime;
    private TextView tvContainerInfo, tvItemTotal, tvTotalAmount, tvPaymentMethod;
    private Button btnSelectTime, btnPaymentMethod, btnPlaceOrder;
    private String selectedPayment = "Paypal"; // default value

    public static OrderSummaryDialog newInstance(WaterContainer container, int quantity) {
        OrderSummaryDialog dialog = new OrderSummaryDialog();
        Bundle args = new Bundle();
        args.putString("container_name", container.getName());
        args.putString("container_price", container.getPrice());
        args.putInt("container_image", container.getImageResourceId());
        args.putInt("quantity", quantity);

        double price = 0;
        try {
            price = Double.parseDouble(container.getPrice());
        } catch (Exception ignored) {}

        double total = price * quantity;
        args.putString("total_amount", String.format("%.2f", total));

        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_dialog_order_summary, container, false);

        initViews(view);
        setupOrderData();
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
        btnBack = view.findViewById(R.id.btn_back);
        btnEditAddress = view.findViewById(R.id.btn_edit_address);
        btnSelectTime = view.findViewById(R.id.btn_select_time);
        btnPaymentMethod = view.findViewById(R.id.btn_payment_method);
        btnPlaceOrder = view.findViewById(R.id.btn_place_order);

        ivContainer = view.findViewById(R.id.iv_container);
        tvCustomerName = view.findViewById(R.id.tv_customer_name);
        tvContactNo = view.findViewById(R.id.tv_contact_no);
        tvAddress = view.findViewById(R.id.tv_address);
        tvPickupTime = view.findViewById(R.id.tv_pickup_time);
        tvContainerInfo = view.findViewById(R.id.tv_container_info);
        tvItemTotal = view.findViewById(R.id.tv_item_total);
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        tvPaymentMethod = view.findViewById(R.id.btn_payment_method);
    }

    private void setupOrderData() {
        Bundle args = getArguments();
        if (args == null) return;

        // Sample static data for now (replace with real session later)
        tvCustomerName.setText("Chrisha Dalmacio");
        tvContactNo.setText("09348502443");
        tvAddress.setText("102 Fairfield St. Brgy BI Cal City");
        tvPickupTime.setText("Not selected");

        // Product info
        String name = args.getString("container_name", "Round Gallon");
        String price = args.getString("container_price", "30");
        int quantity = args.getInt("quantity", 1);
        String totalAmount = args.getString("total_amount", "30.00");

        tvContainerInfo.setText(name + " x" + quantity + " - ₱" + price);
        tvItemTotal.setText("₱" + totalAmount);
        tvTotalAmount.setText("₱" + totalAmount);
        ivContainer.setImageResource(args.getInt("container_image", R.drawable.img_round_container));
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> dismiss());

        btnEditAddress.setOnClickListener(v ->
                showSimpleToast("Address editing coming soon")
        );

        btnSelectTime.setOnClickListener(v ->
                showSimpleToast("Pickup time selection coming soon")
        );

        btnPaymentMethod.setOnClickListener(v -> {
            if (selectedPayment.equals("Paypal")) {
                selectedPayment = "Cash on Delivery";
            } else {
                selectedPayment = "Paypal";
            }
            btnPaymentMethod.setText(selectedPayment);
        });

        btnPlaceOrder.setOnClickListener(v -> {
            Bundle args = getArguments();
            if (args == null) return;

            // For now we just simulate payment process
            String totalAmount = args.getString("total_amount", "30");

            FragmentManager fm = getParentFragmentManager();

            // After clicking place order → show payment dialog
            PaymentMethodDialog paymentDialog = PaymentMethodDialog.newInstance(totalAmount);
            paymentDialog.show(fm, "payment_method");

            dismiss();
        });
    }

    private void showSimpleToast(String message) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show();
    }
}
