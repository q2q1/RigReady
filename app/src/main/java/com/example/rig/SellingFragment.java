package com.example.rig;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rig.adapter.MyListingAdapter;
import com.example.rig.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellingFragment extends Fragment {

    private ListenerRegistration listingsListener;
    private MyListingAdapter adapter;

    private EditText etProductName;
    private EditText etProductPrice;
    private EditText etProductBrand;
    private EditText etProductDescription;

    private View groupForm;
    private Button btnAddNewProduct;
    private Button btnPickImage;
    private Button btnAddProduct;
    private Button btnCancelAdd;
    private ImageView ivProductImage;
    private RecyclerView rvMyListings;

    private Uri selectedImageUri;
    private ActivityResultLauncher<String> imagePicker;

    public SellingFragment() {
        super(R.layout.fragment_selling);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePicker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri == null) return;
            selectedImageUri = uri;
            if (ivProductImage != null) {
                Glide.with(requireContext()).load(uri).centerCrop().into(ivProductImage);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupForm = view.findViewById(R.id.groupForm);
        btnAddNewProduct = view.findViewById(R.id.btnAddNewProduct);
        etProductName = view.findViewById(R.id.etProductName);
        etProductPrice = view.findViewById(R.id.etProductPrice);
        etProductBrand = view.findViewById(R.id.etProductBrand);
        etProductDescription = view.findViewById(R.id.etProductDescription);
        btnPickImage = view.findViewById(R.id.btnPickImage);
        btnAddProduct = view.findViewById(R.id.btnAddProduct);
        btnCancelAdd = view.findViewById(R.id.btnCancelAdd);
        ivProductImage = view.findViewById(R.id.ivProductImage);
        rvMyListings = view.findViewById(R.id.rvMyListings);

        rvMyListings.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MyListingAdapter(product -> {
            if (product.id == null) return;
            if ("sold".equals(product.status)) {
                Toast.makeText(requireContext(), "Sold items cannot be unlisted.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore.getInstance()
                    .collection("products")
                    .document(product.id)
                    .delete()
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(requireContext(), "Unlisted", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Unlist failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
        rvMyListings.setAdapter(adapter);

        btnAddNewProduct.setOnClickListener(v -> {
            groupForm.setVisibility(View.VISIBLE);
        });
        btnCancelAdd.setOnClickListener(v -> {
            groupForm.setVisibility(View.GONE);
            clearForm();
        });
        btnPickImage.setOnClickListener(v -> imagePicker.launch("image/*"));
        btnAddProduct.setOnClickListener(v -> addProduct());

        loadMyListings();
    }

    private void loadMyListings() {
        String uid = getCurrentUid();
        if (uid == null) return;

        listingsListener = FirebaseFirestore.getInstance()
                .collection("products")
                .whereEqualTo("sellerId", uid)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    List<Product> list = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        list.add(Product.from(doc));
                    }
                    adapter.setItems(list);
                });
    }

    private void addProduct() {
        String uid = getCurrentUid();
        if (uid == null) return;

        String name = getText(etProductName);
        String brand = getText(etProductBrand);
        String description = getText(etProductDescription);
        String priceText = getText(etProductPrice);

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(brand) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceText)) {
            Toast.makeText(requireContext(), "Please fill in all product information.", Toast.LENGTH_SHORT).show();
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid price format.", Toast.LENGTH_SHORT).show();
            return;
        }

        // MVP 不依赖 Firebase Storage：不上传图片，只写入 Firestore 的基础信息
        Map<String, Object> product = new HashMap<>();
        product.put("sellerId", uid);
        product.put("name", name);
        product.put("brand", brand);
        product.put("description", description);
        product.put("price", price);
        product.put("imageUrl", ""); // 留空，由 UI 背景色展示占位
        product.put("status", "available");

        FirebaseFirestore.getInstance()
                .collection("products")
                .add(product)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(requireContext(), "Product added", Toast.LENGTH_SHORT).show();
                    clearForm();
                    groupForm.setVisibility(View.GONE);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to add product: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private String getCurrentUid() {
        return FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }

    private String getText(EditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void clearForm() {
        selectedImageUri = null;
        if (ivProductImage != null) ivProductImage.setImageDrawable(null);
        if (etProductName != null) etProductName.setText("");
        if (etProductPrice != null) etProductPrice.setText("");
        if (etProductBrand != null) etProductBrand.setText("");
        if (etProductDescription != null) etProductDescription.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listingsListener != null) {
            listingsListener.remove();
            listingsListener = null;
        }
    }
}

