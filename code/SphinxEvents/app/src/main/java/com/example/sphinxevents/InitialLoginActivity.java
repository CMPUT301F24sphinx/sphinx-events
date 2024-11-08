package com.example.sphinxevents;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class InitialLoginActivity extends AppCompatActivity {

    private String deviceId;
    private DatabaseManager database;
    private UserManager userManager;

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

        userManager = UserManager.getInstance();

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
     *     Validates user input fields. OTHER EXPLANATIONS
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

        Bitmap profilePictureBitmap = TextDrawable.drawableToBitmap(profilePicture);
        String profilePicturePath = userManager.saveBitmapToLocalStorage(this,
                profilePictureBitmap, deviceId);

        Entrant newUser = new Entrant(deviceId, name, email, phone, profilePicturePath,
                "", true, true,
                null, null);

        database.saveUser(newUser, new DatabaseManager.UserCreationCallback() {
            @Override
            public void onSuccess(String deviceId) {
                // Update the current user in UserManager
                userManager.setCurrentUser(newUser);

                // Show success toast
                Toast.makeText(InitialLoginActivity.this, "Profile created successfully!",
                        Toast.LENGTH_SHORT).show();

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

}
