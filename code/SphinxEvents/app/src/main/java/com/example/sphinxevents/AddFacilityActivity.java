/*
 * Allows user to add/edit their facility
 * User can input/edit name, location, and phone number of their facility
 */

package com.example.sphinxevents;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

/**
 * Allows entrant to add/edit their facility
 * User can input/edit name, location, and phone number of their facility
 */
public class AddFacilityActivity extends AppCompatActivity {

    // User and Database attributes
    private UserManager userManager;
    private DatabaseManager databaseManager;
    private Entrant user;

    private String activityContext;

    // XML elements
    private TextView headerTextView;
    private EditText nameEditText;
    private EditText phoneNumberEditText;
    private Button addButton;


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

        // Obtains DatabaseManager, UserManager, and current user
        databaseManager = DatabaseManager.getInstance();
        userManager = UserManager.getInstance();
        user = userManager.getCurrentUser();

        // Obtains editText XML elements
        nameEditText = findViewById(R.id.name_edit_text);
        phoneNumberEditText = findViewById(R.id.phone_number_edit_text);
        headerTextView = findViewById(R.id.adding_facility_header_textview);
        addButton = findViewById(R.id.add_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        // Obtains extra context of activity and changes display if editing facility
        Intent intent = getIntent();
        if (intent.hasExtra("Context")) {
            activityContext = intent.getStringExtra("Context");
            if (activityContext.equals("Edit Facility")) {
                setEditingDisplay((Organizer) user);
            }
        }


        // Implements cancelButton on-click listener
        cancelButton.setOnClickListener(v -> {
            finish();  // Exit activity without changing user and facility
        });

        // Implements addButton on-click listener
        addButton.setOnClickListener(v -> {
            validateInputs();
        });
    }

    /**
     * Validates user inputs
     * Adds/Edits facility if inputs are valid
     */
    public void validateInputs() {
        // Ensures name is entered
        String facilityName = nameEditText.getText().toString();
        if (facilityName.isEmpty()) {
            nameEditText.setError("Name is required");
            return;
        }

        // Ensures phone number is entered and valid
        String facilityPhoneNumber = phoneNumberEditText.getText().toString();
        if (facilityPhoneNumber.isEmpty()) {
            phoneNumberEditText.setError("Phone number is required");
            return ;
        } else if (!InputValidator.isValidPhone(facilityPhoneNumber)) {
            phoneNumberEditText.setError("Invalid phone number");
            return ;
        }


        // Inputs are valid, add or edit the facility depending on activityContext
        if (activityContext.equals("Add Facility")) {
            if (LocationManager.isLocationPermissionGranted(AddFacilityActivity.this)) {
                LocationManager.getLastLocation(AddFacilityActivity.this, new LocationManager.OnLocationReceivedListener() {
                    @Override
                    public void onLocationReceived(Location location) {
                        UserLocation userLocation = new UserLocation(location.getLatitude(), location.getLongitude());
                        addFacility(new Facility(facilityName, userLocation, facilityPhoneNumber, user.getDeviceId()));
                    }

                    @Override
                    public void onLocationError() {
                        Toast.makeText(getApplicationContext(), "Error obtaining location. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                Toast.makeText(getApplicationContext(), "Allow location access to add a facility", Toast.LENGTH_SHORT).show();
            }
        }
        else if (activityContext.equals("Edit Facility")) {
            Facility oldFacility = ((Organizer) user).getFacility();
            if (nothingChanged(oldFacility, facilityName, facilityPhoneNumber)) {
                Toast.makeText(getApplicationContext(), "No changes were made", Toast.LENGTH_SHORT).show();
                finish();
            }
            else {  // Edits facility, uses old facilities' location as new facilities' location
                editFacility(new Facility(facilityName, oldFacility.getLocation(), facilityPhoneNumber, user.getDeviceId()), (Organizer) user);
            }
        }
    }

    /**
     * Saves facility to user's database document
     */
    public void addFacility(Facility newFacility) {
        // Adds facility to database
        databaseManager.addFacility(user.getDeviceId(), newFacility, new DatabaseManager.FacilityCreationCallback() {
            @Override
            public void onSuccess() {
                    user = new Organizer(user.getDeviceId(), user.getName(), user.getEmail(),
                            user.getPhoneNumber(), user.getProfilePictureUrl(), user.isCustomPfp(),
                            user.isOrgNotificationsEnabled(), user.isAdminNotificationsEnabled(),
                            user.getJoinedEvents(), user.getPendingEvents(),
                            newFacility, new ArrayList<>());
                databaseManager.saveUser(user, new DatabaseManager.UserCreationCallback() {
                    @Override
                    public void onSuccess(String deviceId) {
                        Toast.makeText(getApplicationContext(), "Facility added!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getApplicationContext(), "Error adding facility. Please try again.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error adding facility. Please try again.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void editFacility(Facility newFacility, Organizer organizer) {
        databaseManager.addFacility(organizer.getDeviceId(), newFacility, new DatabaseManager.FacilityCreationCallback() {
            @Override
            public void onSuccess() {
                organizer.setFacility(newFacility);
                databaseManager.saveUser(organizer, new DatabaseManager.UserCreationCallback() {
                    @Override
                    public void onSuccess(String deviceId) {
                        Toast.makeText(getApplicationContext(), "Changes saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getApplicationContext(), "Error editing facility. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error editing facility. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sets EditText display when editing facility
     * @param organizer Organizer who has facility
     */
    public void setEditingDisplay(Organizer organizer) {
        Facility facility = organizer.getFacility();
        headerTextView.setText(R.string.editing_facility);
        nameEditText.setText(facility.getName());
        phoneNumberEditText.setText(facility.getPhoneNumber());
        addButton.setText(R.string.save);
    }

    /**
     * Checks if edits to facility contains changes
     *
     * @param oldFacility organizer's old facility
     * @param newName new facility name
     * @param newPhoneNumber new facility phone number
     * @return true if new inputs = old facility attributes, false otherwise
     */
    public boolean nothingChanged(Facility oldFacility, String newName, String newPhoneNumber) {
        return newName.equals(oldFacility.getName()) &&
                newPhoneNumber.equals(oldFacility.getPhoneNumber());
    }
}