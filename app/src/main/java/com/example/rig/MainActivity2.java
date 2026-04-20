package com.example.rig;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    private Button payment;
    private String PublishableKey = "";
    private String SecretKey = "";

    private String CustomersURL = "https://api.stripe.com/v1/customers";
    private String EphericalKeyURL = "https://api.stripe.com/v1/ephemeral_keys";
    private String ClientSecretURL = "https://api.stripe.com/v1/payment_intents";

    private String CustomerId = null;  // Initialize CustomerId as null
    // IMPORTANT: PaymentSheet needs the Ephemeral Key *secret*, not the "id".
    private String EphericalKeySecret;
    private String ClientSecret;
    private PaymentSheet paymentSheet;
    private String Amount = "50000";  // Amount in cents
    private String Currency = "usd";  // Default currency

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        payment = findViewById(R.id.payment);

        PaymentConfiguration.init(this, PublishableKey);

        paymentSheet = new PaymentSheet(this, this::onPaymentResult);

        payment.setOnClickListener(view -> {
            if (CustomerId != null && !CustomerId.isEmpty()) {
                paymentFlow();
            } else {
                Toast.makeText(MainActivity2.this, "Customer ID is not available", Toast.LENGTH_SHORT).show();
            }
        });

        // Create customer and proceed after success
        createCustomer();
    }

    private void createCustomer() {
        StringRequest request = new StringRequest(Request.Method.POST, CustomersURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    CustomerId = object.getString("id");
                    Log.d("Stripe", "Customer created: " + CustomerId);
                    Toast.makeText(MainActivity2.this, "Customer ID: " + CustomerId, Toast.LENGTH_SHORT).show();

                    // Now that CustomerId is available, fetch the Ephemeral Key
                    if (CustomerId != null && !CustomerId.isEmpty()) {
                        getEphericalKey();
                    } else {
                        Toast.makeText(MainActivity2.this, "Failed to create customer", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity2.this, "Error creating customer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity2.this, "Error creating customer: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SecretKey);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getEphericalKey() {
        StringRequest request = new StringRequest(Request.Method.POST, EphericalKeyURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    // Stripe returns { id: "...", object: "ephemeral_key", secret: "ek_test_..." }
                    // PaymentSheet.CustomerConfiguration expects the *secret* value.
                    EphericalKeySecret = object.getString("secret");
                    Log.d("Stripe", "Ephemeral Key secret received");

                    // Now get the Client Secret after the Ephemeral Key is fetched
                    if (EphericalKeySecret != null && !EphericalKeySecret.isEmpty()) {
                        getClientSecret(CustomerId, EphericalKeySecret);
                    } else {
                        Toast.makeText(MainActivity2.this, "Failed to fetch ephemeral key", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity2.this, "Error fetching ephemeral key: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity2.this, "Error fetching ephemeral key: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SecretKey);
                headers.put("Stripe-Version", "2022-11-15");
                return headers;
            }

            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", CustomerId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getClientSecret(String customerId, String ephemeralKey) {
        StringRequest request = new StringRequest(Request.Method.POST, ClientSecretURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    ClientSecret = object.getString("client_secret");
                    Log.d("Stripe", "Client Secret created: " + ClientSecret);
                    Toast.makeText(MainActivity2.this, "Client Secret: " + ClientSecret, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity2.this, "Error fetching client secret: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity2.this, "Error fetching client secret: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SecretKey);
                return headers;
            }

            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerId);
                params.put("amount", Amount);
                params.put("currency", Currency);
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void paymentFlow() {
        if (ClientSecret != null && !ClientSecret.isEmpty()) {
            paymentSheet.presentWithPaymentIntent(ClientSecret, new PaymentSheet.Configuration("Stripe", new PaymentSheet.CustomerConfiguration(
                    CustomerId, EphericalKeySecret
            )));
        } else {
            Toast.makeText(MainActivity2.this, "Client Secret not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {

        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {

            Intent i = new Intent(this, PaymentResultActivity.class);
            i.putExtra(PaymentResultActivity.EXTRA_SUCCESS, true);
            i.putExtra("order_amount", Amount);

            // pass productId from CheckoutActivity
            i.putExtra(
                    PaymentResultActivity.EXTRA_ORDER_ID,
                    getIntent().getStringExtra(CheckoutActivity.EXTRA_PRODUCT_ID)
            );
            startActivity(i);
            finish();
        }

        else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {

            Intent i = new Intent(this, PaymentResultActivity.class);
            i.putExtra(PaymentResultActivity.EXTRA_SUCCESS, false);

            startActivity(i);
            finish();
        }

        else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {

            Intent i = new Intent(this, PaymentResultActivity.class);
            i.putExtra(PaymentResultActivity.EXTRA_SUCCESS, false);

            startActivity(i);
            finish();
        }
    }
}