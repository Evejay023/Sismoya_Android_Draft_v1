package com.example.waterrefilldraftv1.Riders.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterrefilldraftv1.R;
import com.example.waterrefilldraftv1.Riders.Utils.ImageFormatter;
import com.example.waterrefilldraftv1.Riders.Utils.StatusFormatter;
import com.example.waterrefilldraftv1.Riders.models.CompletedOrderModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CompletedOrdersAdapter extends RecyclerView.Adapter<CompletedOrdersAdapter.ViewHolder> {

    private List<CompletedOrderModel> orders;

    public CompletedOrdersAdapter(List<CompletedOrderModel> orders) {
        this.orders = orders != null ? orders : Collections.emptyList();
        sortOrdersByDate();
    }

    private void sortOrdersByDate() {
        if (orders != null && !orders.isEmpty()) {
            Collections.sort(orders, new Comparator<CompletedOrderModel>() {
                @Override
                public int compare(CompletedOrderModel o1, CompletedOrderModel o2) {
                    // Sort by delivered_at date in descending order (newest first)
                    String date1 = o1.getDeliveredAt() != null ? o1.getDeliveredAt() : "";
                    String date2 = o2.getDeliveredAt() != null ? o2.getDeliveredAt() : "";
                    return date2.compareTo(date1);
                }
            });
        }
    }

    public void updateOrders(List<CompletedOrderModel> newOrders) {
        this.orders = newOrders != null ? newOrders : Collections.emptyList();
        sortOrdersByDate();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rider_item_completed_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (orders == null || orders.isEmpty()) {
            return;
        }

        CompletedOrderModel order = orders.get(position);
        if (order == null) {
            return;
        }

        try {
            // Set basic order info with null checks
            if (holder.tvCustomerName != null) {
                holder.tvCustomerName.setText(order.getCustomerName() != null ? order.getCustomerName() : "Customer");
            }

            if (holder.tvDeliveryTime != null) {
                holder.tvDeliveryTime.setText(formatDeliveryTime(order.getDeliveredAt()));
            }

            if (holder.tvAddress != null) {
                holder.tvAddress.setText(order.getAddress() != null ? order.getAddress() : "No address provided");
            }

            if (holder.tvGallonName != null) {
                holder.tvGallonName.setText(order.getPrimaryGallonName());
            }

            if (holder.tvQuantity != null) {
                holder.tvQuantity.setText("Qty: " + order.getPrimaryQuantity());
            }

            // ✅ Use ImageFormatter for safe image loading
            if (holder.ivGallon != null) {
                ImageFormatter.safeLoadGallonImage(
                        holder.ivGallon,
                        order.getPrimaryImageUrl(),
                        order.getPrimaryGallonName()
                );
            }

            // Set click listeners only if views exist
            if (holder.tvViewDetails != null) {
                holder.tvViewDetails.setOnClickListener(v -> {
                    showCompletedOrderDialog(v.getContext(), order);
                });
            }

            // Make entire item clickable
            holder.itemView.setOnClickListener(v -> {
                showCompletedOrderDialog(v.getContext(), order);
            });

        } catch (Exception e) {
            Log.e("CompletedOrdersAdapter", "Error binding view at position " + position, e);
        }
    }

    private String formatDeliveryTime(String deliveredAt) {
        if (deliveredAt == null || deliveredAt.isEmpty()) {
            return "Unknown time";
        }
        try {
            // Format: "11/04/25 12:45AM" -> "Nov 04, 12:45 AM"
            String[] parts = deliveredAt.split(" ");
            if (parts.length >= 2) {
                String datePart = parts[0]; // "11/04/25"
                String timePart = parts[1]; // "12:45AM"

                // Parse date (MM/dd/yy)
                String[] dateParts = datePart.split("/");
                if (dateParts.length == 3) {
                    int month = Integer.parseInt(dateParts[0]);
                    int day = Integer.parseInt(dateParts[1]);
                    String year = "20" + dateParts[2]; // Convert to full year

                    // Format time (add space between time and AM/PM)
                    String formattedTime = timePart.replaceAll("([0-9])([AP]M)", "$1 $2");

                    return String.format("Delivered: %02d/%02d/%s %s", month, day, year, formattedTime);
                }
            }
            return "Delivered: " + deliveredAt; // Return original if parsing fails
        } catch (Exception e) {
            return "Delivered: " + deliveredAt; // Return original if any error
        }
    }

    private void showCompletedOrderDialog(Context context, CompletedOrderModel order) {
        try {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.rider_dialog_completed_order);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            // Find all views with null checks
            TextView tvOrderId = dialog.findViewById(R.id.tv_order_id);
            TextView tvCustomerName = dialog.findViewById(R.id.tv_customer_name);
            TextView tvContactNo = dialog.findViewById(R.id.tv_contact_no);
            TextView tvAddress = dialog.findViewById(R.id.tv_address);
            TextView tvTotalAmount = dialog.findViewById(R.id.tv_total_amount);
            TextView tvPaymentMethod = dialog.findViewById(R.id.tv_payment_method);
            TextView tvOrderStatus = dialog.findViewById(R.id.tv_order_status);
            TextView tvDeliveredAt = dialog.findViewById(R.id.tv_delivered_at);
            ImageView ivGallon = dialog.findViewById(R.id.iv_gallon_image);
            TextView tvGallonName = dialog.findViewById(R.id.tv_gallon_name);
            TextView tvQuantity = dialog.findViewById(R.id.tv_quantity);
            ImageView btnClose = dialog.findViewById(R.id.btn_close);

            // Set order details with null checks
            if (tvOrderId != null) tvOrderId.setText(String.valueOf(order.getOrderId()));
            if (tvCustomerName != null) tvCustomerName.setText(order.getCustomerName());
            if (tvContactNo != null) tvContactNo.setText(order.getContactNo() != null ? order.getContactNo() : "N/A");
            if (tvAddress != null) tvAddress.setText(order.getAddress());
            if (tvTotalAmount != null) tvTotalAmount.setText(order.getFormattedTotal());
            if (tvPaymentMethod != null) tvPaymentMethod.setText(order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A");
            if (tvOrderStatus != null) tvOrderStatus.setText(StatusFormatter.format(order.getStatus()));
            if (tvDeliveredAt != null) tvDeliveredAt.setText(formatDeliveryTime(order.getDeliveredAt()));
            if (tvGallonName != null) tvGallonName.setText(order.getPrimaryGallonName());
            if (tvQuantity != null) tvQuantity.setText("Quantity: " + order.getPrimaryQuantity());

            // ✅ Use ImageFormatter for dialog image
            if (ivGallon != null) {
                ImageFormatter.safeLoadGallonImage(
                        ivGallon,
                        order.getPrimaryImageUrl(),
                        order.getPrimaryGallonName()
                );
            }

            if (btnClose != null) {
                btnClose.setOnClickListener(v -> dialog.dismiss());
            }

            dialog.show();
        } catch (Exception e) {
            Log.e("CompletedOrdersAdapter", "Error showing dialog", e);
            Toast.makeText(context, "Error showing order details", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGallon;
        TextView tvCustomerName, tvDeliveryTime, tvAddress, tvViewDetails, tvGallonName, tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize all views with null checks
            ivGallon = itemView.findViewById(R.id.iv_gallon_image);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvDeliveryTime = itemView.findViewById(R.id.tv_delivery_time);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvViewDetails = itemView.findViewById(R.id.tv_view_details);
            tvGallonName = itemView.findViewById(R.id.tv_gallon_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);

            // Log if any view is null for debugging
            if (ivGallon == null) Log.w("ViewHolder", "ivGallon is null");
            if (tvCustomerName == null) Log.w("ViewHolder", "tvCustomerName is null");
            if (tvDeliveryTime == null) Log.w("ViewHolder", "tvDeliveryTime is null");
            if (tvAddress == null) Log.w("ViewHolder", "tvAddress is null");
            if (tvViewDetails == null) Log.w("ViewHolder", "tvViewDetails is null");
            if (tvGallonName == null) Log.w("ViewHolder", "tvGallonName is null");
            if (tvQuantity == null) Log.w("ViewHolder", "tvQuantity is null");
        }
    }
}