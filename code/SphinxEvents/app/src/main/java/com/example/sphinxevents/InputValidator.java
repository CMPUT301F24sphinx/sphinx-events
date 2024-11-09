/*
 * Class Name: InputVariable
 * Date: 2024-11-08
 *
 *
 * Description:
 * The InputValidator class provides simple methods to check if an email or phone number is valid.
 * It helps ensure that the input data meets standard formats, making it easier to validate user
 * information in applications.
 */

package com.example.sphinxevents;

import android.util.Patterns;

/**
 * A utility class for validating common types of input data.
 */
public class InputValidator {

    /**
     * Checks if the given email is valid based on standard email patterns
     *
     * @param email The email to validate
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Checks if the given phone number is valid based on standard phone patterns.
     *
     * @param phoneNumber The phone number to validate.
     * @return true if the phone number is valid, false otherwise.
     */
    public static boolean isValidPhone(String phoneNumber) {
        return Patterns.PHONE.matcher(phoneNumber).matches();
    }
}
