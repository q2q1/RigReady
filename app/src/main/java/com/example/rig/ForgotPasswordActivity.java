package com.example.rig;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etForgotEmail;
    private Button btnSendReset;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etForgotEmail = findViewById(R.id.etForgotEmail);
        btnSendReset = findViewById(R.id.btnSendReset);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        btnSendReset.setOnClickListener(v -> attemptReset());
        tvBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptReset() {
        String email = etForgotEmail.getText() != null ? etForgotEmail.getText().toString().trim() : "";
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "If an account exists for this email, a reset link was sent. Check inbox and spam.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    } else {
                        String msg = task.getException() != null ? task.getException().getMessage() : "Reset failed";
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

