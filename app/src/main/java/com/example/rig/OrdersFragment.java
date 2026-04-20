package com.example.rig;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rig.adapter.OrderAdapter;
import com.example.rig.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private ListenerRegistration ordersListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OrdersFragment() {
        super(R.layout.fragment_orders);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvOrders = view.findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));

        OrderAdapter adapter = new OrderAdapter();
        rvOrders.setAdapter(adapter);

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        if (uid == null) {
            return;
        }

        ordersListener = FirebaseFirestore.getInstance()
                .collection("orders")
                .whereEqualTo("buyerId", uid)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    List<Order> list = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        System.out.println("ORDER DATA: " + doc.getData());
                        list.add(Order.from(doc));
                    }
                    adapter.setItems(list);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (ordersListener != null) {
            ordersListener.remove();
            ordersListener = null;
        }
    }
}

