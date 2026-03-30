package com.example.rig.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rig.R;
import com.example.rig.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> items = new ArrayList<>();

    public void setItems(List<Order> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order o = items.get(position);
        holder.tvOrderId.setText("Order #" + (o.id != null ? o.id : ""));
        holder.tvOrderProduct.setText(o.productName != null ? o.productName : "");
        holder.tvOrderPrice.setText(String.format("$%.2f", o.price));
        holder.tvOrderStatus.setText(o.status != null ? o.status : "");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId;
        TextView tvOrderProduct;
        TextView tvOrderPrice;
        TextView tvOrderStatus;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderProduct = itemView.findViewById(R.id.tvOrderProduct);
            tvOrderPrice = itemView.findViewById(R.id.tvOrderPrice);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
        }
    }
}

