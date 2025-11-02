package com.example.waterrefilldraftv1.Customer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterrefilldraftv1.Customer.models.Address;
import com.example.waterrefilldraftv1.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private final List<Address> addresses;
    private final OnAddressClickListener listener;

    public interface OnAddressClickListener {
        void onAddressClick(Address address);
    }

    public AddressAdapter(List<Address> addresses, OnAddressClickListener listener) {
        this.addresses = addresses;
        this.listener = listener;
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item_address_card, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
        Address address = addresses.get(position);

        // Set label (e.g., "Home", "Office")
        holder.tvLabel.setText(address.getLabel());

        // Set address with "Address: " prefix to match UI
        holder.tvAddress.setText("Address: " + address.getAddress());

        // Show or hide default badge
        if (address.isDefault()) {
            holder.tvDefaultBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvDefaultBadge.setVisibility(View.GONE);
        }

        holder.cardView.setOnClickListener(v -> listener.onAddressClick(address));
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvAddress, tvDefaultBadge;
        CardView cardView;

        public AddressViewHolder(View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tv_label);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvDefaultBadge = itemView.findViewById(R.id.tv_default_badge);
            cardView = itemView.findViewById(R.id.card_address);
        }
    }
}