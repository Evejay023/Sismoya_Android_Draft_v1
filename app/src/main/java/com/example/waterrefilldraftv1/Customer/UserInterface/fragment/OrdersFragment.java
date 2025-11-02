package com.example.waterrefilldraftv1.Customer.UserInterface.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.waterrefilldraftv1.R;

public class OrdersFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_fragment_orders, container, false);

        LinearLayout list = view.findViewById(R.id.orders_list_container);
        TextView empty = view.findViewById(R.id.tv_orders_placeholder);



        return view;
    }


}