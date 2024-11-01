
package com.example.sphinxevents;

import android.util.Patterns;

public class InputValidator {

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhone(String phoneNumber) {
        return Patterns.PHONE.matcher(phoneNumber).matches();
    }
}
