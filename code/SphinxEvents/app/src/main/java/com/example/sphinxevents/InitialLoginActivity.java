package com.example.sphinxevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
     *     Validates user input fields. OTHER EXPLANATIONS
     * </p>
     */
    private void createProfile() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        // Basic validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> joinedEvents = new ArrayList<String>();
        ArrayList<String> pendingEvents = new ArrayList<String>();

        Entrant newUser = new Entrant(deviceId, name, email, phone, "",
                joinedEvents, pendingEvents);

        database.saveUser(newUser, new DatabaseManager.UserCreationCallback() {
            @Override
            public void onSuccess(String deviceId) {
                // Show success toast
                Toast.makeText(InitialLoginActivity.this, "Profile created successfully!",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(InitialLoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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