/*
 * Class Name: InitialLoginActivity
 * Date: 2024-11-06
 *
 * Copyright (c) 2024
 * All rights reserved.
 *
 */

package com.example.sphinxevents;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

/**
 * InitialLoginActivity is the activity that handles the initial login process for users.
 * Users are allowed to implement a name, email, and optional phone number. The activity also
 * conducts basic input validation. A deterministic profile picture based on the users name is
 * generated when the user is ready to create their profile. The user data is stored in the
 * Firebase Firestore database.
 */
public class InitialLoginActivity extends AppCompatActivity {

    private String deviceId;
    private DatabaseManager database;

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_initial_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        deviceId = getIntent().getStringExtra("DEVICE_ID");
        database = DatabaseManager.getInstance();

        nameEditText = findViewById(R.id.initial_login_name_edit_text);
        emailEditText = findViewById(R.id.initial_login_email_edit_text);
        phoneEditText = findViewById(R.id.initial_login_phone_edit_text);
        Button createProfileButton = findViewById(R.id.create_profile_button);

        // Set a click listener to the create profile button
        createProfileButton.setOnClickListener(v -> createProfile());
    }

    /**
     * Creates a new user profile based on input from the user
     * <p>
     *     Validates user input fields, creates an Entrant object to represent the user,
     *     generates a deterministic profile picture based on the first character of the user's
     *     name, stores the user in the Firebase Firestore database.
     * </p>
     */
    private void createProfile() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        // Basic validation
        if (name.isEmpty()){
            nameEditText.setError("Please enter your name");
            return;
        } else if (email.isEmpty()) {
            emailEditText.setError("Email cannot be empty");
            return;
        } else if (!InputValidator.isValidEmail(email)) {
            emailEditText.setError("Invalid email address");
            return;
        } else if (!phone.isEmpty() && !InputValidator.isValidPhone(phone)) {
            phoneEditText.setError("Invalid phone number");
            return;
        }

        // Generate deterministic profile picture for user
        Drawable profilePicture = TextDrawable.createTextDrawable(
                this,
                String.valueOf(name.charAt(0)),
                Color.WHITE,
                140
        );

        try {
            // Convert Drawable to Bitmap
            Bitmap bitmap = ProfilePictureHelper.drawableToBitmap(profilePicture, 140, 140);

            // Save Bitmap to a temporary file and get its URI
            Uri pfpUri = ProfilePictureHelper.saveBitmapToFile(this, bitmap, "profile_picture.png");

            // Upload the profile picture to Firebase Storage
            DatabaseManager databaseManager = new DatabaseManager();
            databaseManager.uploadProfilePicture(deviceId, pfpUri, new DatabaseManager.UploadProfilePictureCallback() {
                @Override
                public void onSuccess(String profilePictureUrl) {
                    // Create new Entrant object with the profile picture URL
                    Entrant newUser = new Entrant(deviceId, name, email, phone,
                            profilePictureUrl, false, true, true,
                            null, null);

                    // Save the user to the database
                    database.saveUser(newUser, new DatabaseManager.UserCreationCallback() {
                        @Override
                        public void onSuccess(String deviceId) {
                            // Show success toast
                            Toast.makeText(InitialLoginActivity.this, "Profile created successfully!",
                                    Toast.LENGTH_SHORT).show();

                            // Ask user to allow location
                            LocationManager.requestLocationPermission(InitialLoginActivity.this);

                            finish();  // Close InitialLoginActivity
                        }

                        @Override
                        public void onFailure(Exception e) {
                            // Show failure toast
                            Toast.makeText(InitialLoginActivity.this, "Failed to create profile: " +
                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure() {
                    // Show failure toast if profile picture upload fails
                    Toast.makeText(InitialLoginActivity.this, "Failed to upload profile picture.", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            // Handle file saving error
            e.printStackTrace();
            Toast.makeText(this, "Error saving profile picture.", Toast.LENGTH_SHORT).show();
        }
    }
}
