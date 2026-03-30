package com.example.rig;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "extra_product_id";

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etAddress;
    private RadioGroup rgPayment;
    private Button btnPayNow;

    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etAddress = findViewById(R.id.etAddress);
        rgPayment = findViewById(R.id.rgPayment);
        btnPayNow = findViewById(R.id.btnPayNow);

        productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        if (productId == null || productId.isEmpty()) {
            Toast.makeText(this, "Invalid product ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnPayNow.setOnClickListener(v -> goToStripe());
    }

    private void goToStripe() {
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        if (uid == null) {
            Toast.makeText(this, "Please sign in first.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String firstName = getText(etFirstName);
        String lastName = getText(etLastName);
        String address = getText(etAddress);
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please fill in complete shipping details.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Stripe PaymentSheet requires a backend-created PaymentIntent client_secret.
        // This project will continue once you fill keys + backend wiring in MainActivity2.
        startActivity(new Intent(this, MainActivity2.class));
    }

    private String getPaymentMethod() {
        int checked = rgPayment.getCheckedRadioButtonId();
        RadioButton rb = findViewById(checked);
        if (rb == null) return "credit_card";
        if (rb.getId() == R.id.rbPaypal) return "paypal";
        return "credit_card";
    }

    private String getText(EditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}

