package com.example.rig;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.Locale;

/**
 * Client-side email checks. Firebase cannot prove a mailbox exists; this only blocks obvious junk
 * and limits sign-up to Gmail domains (course/demo requirement).
 */
public final class EmailRules {

    private EmailRules() {
    }

    /**
     * @return null if OK, otherwise an English error message for Toast.
     */
    public static String registrationEmailError(String email) {
        if (TextUtils.isEmpty(email)) {
            return "Please enter an email address.";
        }
        String trimmed = email.trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
            return "Please enter a valid email address.";
        }
        String lower = trimmed.toLowerCase(Locale.US);
        // Only Gmail / Google Mail (same inbox). Remove this block if you need other providers.
        if (!lower.endsWith("@gmail.com") && !lower.endsWith("@googlemail.com")) {
            return "Registration is limited to Gmail addresses (@gmail.com).";
        }
        return null;
    }
}
