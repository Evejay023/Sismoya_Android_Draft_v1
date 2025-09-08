package com.example.waterrefilldraftv1;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddToCartDialog extends DialogFragment {

    private ContainersFragment.WaterContainer container;
    private ImageView ivContainer;
    private TextView tvContainerType, tvContainerLiters, tvContainerPrice;
    private EditText etQuantity;
    private Button btnNext, btnClose;

    public static AddToCartDialog newInstance(ContainersFragment.WaterContainer container) {
        AddToCartDialog dialog = new AddToCartDialog();
        Bundle args = new Bundle();
        args.putInt("container_id", container.getId());
        args.putString("container_name", container.getName());
        args.putString("container_liters", container.getLiters());
        args.putString("container_price", container.getPrice());
        args.putInt("container_image", container.getImageResourceId());
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_to_cart, container, false);

        initViews(view);
        setupContainerData();
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
        tvContainerType = view.findViewById(R.id.tv_container_type);
        tvContainerLiters = view.findViewById(R.id.tv_container_liters);
        tvContainerPrice = view.findViewById(R.id.tv_container_price);
        etQuantity = view.findViewById(R.id.et_quantity);
        btnNext = view.findViewById(R.id.btn_next);
        btnClose = view.findViewById(R.id.btn_close);
    }

    private void setupContainerData() {
        Bundle args = getArguments();
        if (args != null) {
            tvContainerType.setText("Type: " + args.getString("container_name", ""));
            tvContainerLiters.setText("Liters: " + args.getString("container_liters", ""));
            tvContainerPrice.setText("Price: " + args.getString("container_price", ""));
            ivContainer.setImageResource(args.getInt("container_image", R.drawable.round_container));
            etQuantity.setText("1");
        }
    }

    private void setupClickListeners() {
        btnClose.setOnClickListener(v -> dismiss());

        btnNext.setOnClickListener(v -> {
            int quantity = 1;
            try {
                quantity = Integer.parseInt(etQuantity.getText().toString());
            } catch (NumberFormatException e) {
                quantity = 1;
            }

            // Navigate to order summary
            Bundle args = getArguments();
            if (args != null) {
                ContainersFragment.WaterContainer container = new ContainersFragment.WaterContainer(
                        args.getInt("container_id"),
                        args.getString("container_name", ""),
                        args.getString("container_liters", ""),
                        args.getString("container_price", ""),
                        args.getInt("container_image"),
                        true
                );

                OrderSummaryDialog orderDialog = OrderSummaryDialog.newInstance(container, quantity);
                orderDialog.show(getParentFragmentManager(), "order_summary");
                dismiss();
            }
        });
    }
}