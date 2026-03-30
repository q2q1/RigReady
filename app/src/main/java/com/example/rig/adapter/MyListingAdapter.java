package com.example.rig.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rig.R;
import com.example.rig.model.Product;

import java.util.ArrayList;
import java.util.List;

public class MyListingAdapter extends RecyclerView.Adapter<MyListingAdapter.MyListingViewHolder> {

    public interface OnUnlistClickListener {
        void onUnlist(Product product);
    }

    private final OnUnlistClickListener listener;
    private final List<Product> items = new ArrayList<>();

    public MyListingAdapter(OnUnlistClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Product> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_listing, parent, false);
        return new MyListingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyListingViewHolder holder, int position) {
        Product p = items.get(position);

        holder.tvListingName.setText(p.name != null ? p.name : "");
        holder.tvListingPrice.setText(String.format("$%.2f", p.price));
        holder.tvListingStatus.setText(p.status != null ? p.status : "");

        if (p.imageUrl != null && !p.imageUrl.isEmpty()) {
            Glide.with(holder.ivListingImage.getContext()).load(p.imageUrl).centerCrop().into(holder.ivListingImage);
        } else {
            holder.ivListingImage.setImageDrawable(null);
        }

        holder.btnUnlist.setEnabled(p.id != null && !"sold".equals(p.status));
        holder.btnUnlist.setOnClickListener(v -> listener.onUnlist(p));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyListingViewHolder extends RecyclerView.ViewHolder {
        ImageView ivListingImage;
        TextView tvListingName;
        TextView tvListingPrice;
        TextView tvListingStatus;
        Button btnUnlist;

        MyListingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivListingImage = itemView.findViewById(R.id.ivListingImage);
            tvListingName = itemView.findViewById(R.id.tvListingName);
            tvListingPrice = itemView.findViewById(R.id.tvListingPrice);
            tvListingStatus = itemView.findViewById(R.id.tvListingStatus);
            btnUnlist = itemView.findViewById(R.id.btnUnlist);
        }
    }
}

