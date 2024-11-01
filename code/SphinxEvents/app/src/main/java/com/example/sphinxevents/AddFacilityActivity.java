package com.example.sphinxevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class AddFacilityActivity extends AppCompatActivity {

    // User and Database attributes
    private UserManager userManager;
    private Entrant user;
    private DatabaseManager databaseManager;

    private String context;  // stores extra context of activity

    // XML elements
    private EditText nameEditText;
    private EditText locationEditText;
    private EditText phoneNumberEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_facility);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtains extra context of activity if present
        Intent intent = getIntent();
        if (intent.hasExtra("Context")) {
            context = intent.getStringExtra("Context");
        }

        // Obtains DatabaseManager, UserManager, and current user
        databaseManager = DatabaseManager.getInstance();
        userManager = UserManager.getInstance();
        user = userManager.getCurrentUser();

        // Obtains editText XML elements
        nameEditText = findViewById(R.id.name_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        phoneNumberEditText = findViewById(R.id.phone_number_edit_text);
        TextView headerTextView = findViewById(R.id.adding_facility_header_textview);
        Button addButton = findViewById(R.id.add_button);
        if (context.equals("Edit Facility")) {
            headerTextView.setText(R.string.editing_facility);
            addButton.setText(R.string.save);  // sets text of button to "Save" instead of "Add"
        }

        // Implements cancelButton on-click listener
        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
            finish();  // Exit activity without changing user and facility
        });

        // Implements addButton on-click listener
        addButton.setOnClickListener(v -> {
            addFacility();
        });
    }

    /**
     * Validates user input for facility
     * Adds facility to database if input in valid
     * updates user in UserManager
     */
    public void addFacility() {
        // Ensures name is entered
        String facilityName = nameEditText.getText().toString();
        if (facilityName.isEmpty()) {
            nameEditText.setError("Name is required");
            return;
        }

        // Ensures location is entered
        // TODO: other location verifications
        String facilityLocation = locationEditText.getText().toString();
        if (facilityLocation.isEmpty()) {
            locationEditText.setError("Location is required");
            return;
        }

        // Ensures phone number is entered
        // TODO: other phone number verifications
        String facilityPhoneNumber = phoneNumberEditText.getText().toString();
        if (facilityPhoneNumber.isEmpty()) {
            phoneNumberEditText.setError("Phone number is required");
            return;
        }

        Facility newFacility = new Facility(facilityName, facilityLocation, facilityPhoneNumber);

        // Adds facility to database
        databaseManager.addFacility(user.getDeviceId(), newFacility, new DatabaseManager.FacilityCreationCallback() {
            @Override
            public void onSuccess(Facility facility) {
                user = new Organizer(user.getDeviceId(), user.getName(), user.getEmail(),
                        user.getPhoneNumber(), user.getProfilePicture(), user.getJoinedEvents(),
                        user.getPendingEvents(), facility, new ArrayList<>());
                userManager.setCurrentUser(user);  // update user
                finish();
            }
            @Override
            public void onFailure(Exception e) {
                finish();
            }
        });
    }
}