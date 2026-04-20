package com.example.rig;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PaymentResultActivity extends AppCompatActivity {

    public static final String EXTRA_SUCCESS = "extra_success";
    public static final String EXTRA_ORDER_ID = "extra_order_id";

    private TextView tvResultTitle;
    private TextView tvResultMessage;
    private Button btnBackOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvResultMessage = findViewById(R.id.tvResultMessage);
        btnBackOrders = findViewById(R.id.btnBackOrders);

        boolean success = getIntent().getBooleanExtra(EXTRA_SUCCESS, false);
        if (success) {
            tvResultTitle.setText("Payment Successful");
            tvResultMessage.setText("Your order has been created.");
            saveOrder();
        } else {
            tvResultTitle.setText("Payment Failed");
            tvResultMessage.setText("Please try again.");
        }

        btnBackOrders.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(MainActivity.EXTRA_TAB_ID, R.id.tab_orders);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }

    private void saveOrder() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String amountStr = getIntent().getStringExtra("order_amount");
        double amount = Double.parseDouble(amountStr) / 100.0;

        Map<String, Object> order = new HashMap<>();
        order.put("buyerId", uid);
        order.put("price", amount);
        order.put("status", "paid");
        order.put("timestamp", System.currentTimeMillis());

        db.collection("orders").add(order);
    }
}

