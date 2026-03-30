package com.example.rig;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rig.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "extra_product_id";

    private ImageView ivProduct;
    private TextView tvName;
    private TextView tvPrice;
    private TextView tvBrand;
    private TextView tvDescription;
    private Button btnBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ivProduct = findViewById(R.id.ivProduct);
        tvName = findViewById(R.id.tvName);
        tvPrice = findViewById(R.id.tvPrice);
        tvBrand = findViewById(R.id.tvBrand);
        tvDescription = findViewById(R.id.tvDescription);
        btnBuy = findViewById(R.id.btnBuy);

        String productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        if (productId == null || productId.isEmpty()) {
            Toast.makeText(this, "Invalid product ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseFirestore.getInstance().collection("products").document(productId).get()
                .addOnSuccessListener(this::onLoaded)
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load product: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        btnBuy.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                    : null;
            // uid 为空通常表示未登录；但 MainActivity 已做登录守卫
            if (uid == null) {
                Toast.makeText(this, "Please sign in first.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Intent i = new Intent(this, CheckoutActivity.class);
            i.putExtra(CheckoutActivity.EXTRA_PRODUCT_ID, productId);
            startActivity(i);
        });
    }

    private void onLoaded(DocumentSnapshot doc) {
        Product p = Product.from(doc);
        tvName.setText(p.name != null ? p.name : "");
        tvPrice.setText(String.format("$%.2f", p.price));
        tvBrand.setText(p.brand != null ? p.brand : "");
        tvDescription.setText(p.description != null ? p.description : "");

        if (p.imageUrl != null && !p.imageUrl.isEmpty()) {
            Glide.with(this).load(p.imageUrl).centerCrop().into(ivProduct);
        } else {
            ivProduct.setImageDrawable(null);
        }
    }
}

