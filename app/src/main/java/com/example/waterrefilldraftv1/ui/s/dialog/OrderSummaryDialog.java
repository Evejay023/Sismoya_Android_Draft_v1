package com.example.waterrefilldraftv1.ui.s.dialog;

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

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.models.WaterContainer;

public class OrderSummaryDialog extends DialogFragment {

    private ImageView ivContainer;
    private TextView tvCustomerName, tvContactNo, tvAddress, tvPickupTime;
    private TextView tvContainerType, tvQuantity, tvTotalAmount;
    private Button btnPlaceOrder;

    public static OrderSummaryDialog newInstance(WaterContainer container, int quantity) {
        OrderSummaryDialog dialog = new OrderSummaryDialog();
        Bundle args = new Bundle();
        args.putString("container_name", container.getName());
        args.putString("container_price", container.getPrice());
        args.putInt("container_image", container.getImageResourceId());
        args.putInt("quantity", quantity);

        // Calculate total
        double price = Double.parseDouble(container.getPrice());
        double total = price * quantity;
        args.putString("total_amount", String.valueOf((int)total));

        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_order_summary, container, false);

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
        ivContainer = view.findViewById(R.id.iv_container);
        tvCustomerName = view.findViewById(R.id.tv_customer_name);
        tvContactNo = view.findViewById(R.id.tv_contact_no);
        tvAddress = view.findViewById(R.id.tv_address);
        tvPickupTime = view.findViewById(R.id.tv_pickup_time);
        tvContainerType = view.findViewById(R.id.tv_container_type);
        tvQuantity = view.findViewById(R.id.tv_quantity);
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        btnPlaceOrder = view.findViewById(R.id.btn_place_order);
    }

    private void setupOrderData() {
        Bundle args = getArguments();
        if (args != null) {
            // Sample customer data (in real app, get from user session)
            tvCustomerName.setText("Customer Name: Chrisha Dalmacio");
            tvContactNo.setText("Contact No: 09348502443");
            tvAddress.setText("Address: 102 Fairfield St. Brgy BI Cal City");
            tvPickupTime.setText("Pickup Time: August 23, 2025 10:00AM");

            tvContainerType.setText(args.getString("container_name", "") + " x1");
            tvQuantity.setText("Quantity: " + args.getInt("quantity", 1));
            tvTotalAmount.setText("Total Amount: " + args.getString("total_amount", "30"));

            ivContainer.setImageResource(args.getInt("container_image", R.drawable.img_round_container));
        }
    }

    private void setupClickListeners() {
        btnPlaceOrder.setOnClickListener(v -> {
            // Navigate to payment method
            PaymentMethodDialog paymentDialog = PaymentMethodDialog.newInstance(
                    getArguments().getString("total_amount", "30")
            );
            paymentDialog.show(getParentFragmentManager(), "payment_method");
            dismiss();
        });
    }
}