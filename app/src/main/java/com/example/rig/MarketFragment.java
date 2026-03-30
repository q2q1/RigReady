package com.example.rig;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rig.adapter.ProductAdapter;
import com.example.rig.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class MarketFragment extends Fragment {

    private ListenerRegistration productsListener;

    public MarketFragment() {
        super(R.layout.fragment_market);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvMarket = view.findViewById(R.id.rvMarket);
        rvMarket.setLayoutManager(new LinearLayoutManager(requireContext()));

        ProductAdapter adapter = new ProductAdapter(product -> {
            Intent i = new Intent(requireContext(), ProductDetailActivity.class);
            i.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.id);
            startActivity(i);
        });
        rvMarket.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        productsListener = db.collection("products")
                .whereEqualTo("status", "available")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    List<Product> list = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        list.add(Product.from(doc));
                    }
                    adapter.setItems(list);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (productsListener != null) {
            productsListener.remove();
            productsListener = null;
        }
    }
}

