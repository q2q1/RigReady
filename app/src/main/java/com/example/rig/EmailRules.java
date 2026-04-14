package com.example.rig;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.Locale;


public final class EmailRules {

    private EmailRules() {
    }


    public static String registrationEmailError(String email) {
        if (TextUtils.isEmpty(email)) {
            return "Please enter an email address.";
        }
        String trimmed = email.trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
            return "Please enter a valid email address.";
        }
        String lower = trimmed.toLowerCase(Locale.US);

        if (!lower.endsWith("@gmail.com") && !lower.endsWith("@googlemail.com")) {
            return "Registration is limited to Gmail addresses (@gmail.com).";
        }
        return null;
    }
}
