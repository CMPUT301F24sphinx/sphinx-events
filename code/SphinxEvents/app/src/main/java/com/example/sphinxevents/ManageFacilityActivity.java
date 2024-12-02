/*
 * Displays user's facility information if they have one
 * Gives Entrants the option to add a facility to their profile
 * Gives Organizers the option to remove or edit their facility
 */

package com.example.sphinxevents;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.content.pm.PackageManager;

/**
 * Displays user's facility information if they have one
 * Gives Entrants the option to add a facility to their profile
 * Gives Organizers the option to remove or edit their facility
 */
public class ManageFacilityActivity extends AppCompatActivity
        implements UserManager.UserUpdateListener {

    // Facility and user related attributes
    private DatabaseManager databaseManager;
    private UserManager userManager;
    private Entrant user;

    // XML elements that change depending on user's role
    private TextView youHaveNoFacilityTextView;
    private Button addFacilityButton;
    private TextView yourFacilityTextView;
    private LinearLayout facilityNameLayout;
    private TextView facilityNameTextView;
    private LinearLayout facilityPhoneNumberLayout;
    private TextView facilityPhoneNumberTextView;
    private LinearLayout removeEditButtonLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_facility);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtains current user and sets
        userManager = UserManager.getInstance();
        user = userManager.getCurrentUser();
        userManager.addUserUpdateListener(this);
        userManager.startListeningForUserChanges(user.getDeviceId());
        databaseManager = DatabaseManager.getInstance();

        // Obtains relevant XML elements
        youHaveNoFacilityTextView = findViewById(R.id.you_have_no_facility_textview);
        addFacilityButton = findViewById(R.id.add_facility_button);
        yourFacilityTextView = findViewById(R.id.your_facility_textview);
        facilityNameLayout = findViewById(R.id.facility_name_layout);
        facilityNameTextView = findViewById(R.id.facility_name_textview);
        facilityPhoneNumberLayout = findViewById(R.id.facility_phone_number_layout);
        facilityPhoneNumberTextView = findViewById(R.id.facility_phone_number_textview);
        removeEditButtonLayout = findViewById(R.id.remove_edit_button_layout);
        Button editFacilityButton = findViewById(R.id.edit_facility_button);
        Button removeFacilityButton = findViewById(R.id.remove_facility_button);

        setDisplay();  // Sets screen display based on user role

        // Clicking backButton -> finish activity
        ImageButton backButton = findViewById(R.id.manage_facility_back_btn);
        backButton.setOnClickListener(v -> {
            finish();  // close activity when back arrow is pressed
        });

        // Clicking addFacilityButton -> start addFacilityActivity
        addFacilityButton.setOnClickListener(v -> {
            if (isLocationPermissionGranted()) {
                Intent addFacilityIntent = new Intent(ManageFacilityActivity.this, AddFacilityActivity.class);
                addFacilityIntent.putExtra("Context", "Add Facility");
                startActivity(addFacilityIntent);
            }
            else {
                Toast.makeText(getApplicationContext(), "Allow location access to add a facility", Toast.LENGTH_SHORT).show();
            }
        });

        // Clicking editFacilityButton -> start addFacilityActivity with extra context
        editFacilityButton.setOnClickListener(v -> {
            Intent editFacilityIntent = new Intent(ManageFacilityActivity.this, AddFacilityActivity.class);
            editFacilityIntent.putExtra("Context", "Edit Facility");
            startActivity(editFacilityIntent);
        });

        // Clicking removeFacilityButton -> remove facility from user's profile
        removeFacilityButton.setOnClickListener(v -> {
            deleteFacility();
        });
    }

    /**
     * Removes facility from user's profile
     */
    public void deleteFacility() {
        databaseManager.removeFacility(user.getDeviceId(), new DatabaseManager.FacilityRemovalCallback() {
            @Override
            public void onSuccess(Entrant updatedUser) {
                Toast.makeText(getApplicationContext(), "Facility removed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error adding facility.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    /**
     * Removes this activity from UserUpdateListener list when activity closes
     */
    @Override
    protected void onDestroy() {
        UserManager.getInstance().removeUserUpdateListener(this);  // Unlisted ManageFacilityActivity as a listener
        super.onDestroy();
    }

    /**
     * Updates display when user facility is added, edited, or removed
     */
    @Override
    public void onUserUpdated() {
        user = userManager.getCurrentUser();
        setDisplay();
    }

    /**
     * Sets the visibility of XML elements based on whether user is organizer or not
     */
    public void setDisplay() {
        int entrantVisibility = View.VISIBLE;  // state of entrant-related XML
        int organizerVisibility = View.GONE;  // state of organizer-related XML

        // changes visibility values if user is Organizer
        if (user.getRole().equals("Organizer")) {
            entrantVisibility = View.GONE;
            organizerVisibility = View.VISIBLE;
            setOrganizerDisplay((Organizer) user);  // sets TextView values for user's facility
        }

        // sets visibility of XML elements
        youHaveNoFacilityTextView.setVisibility(entrantVisibility);
        addFacilityButton.setVisibility(entrantVisibility);
        yourFacilityTextView.setVisibility(organizerVisibility);
        facilityNameLayout.setVisibility(organizerVisibility);
        facilityPhoneNumberLayout.setVisibility(organizerVisibility);
        removeEditButtonLayout.setVisibility(organizerVisibility);
    }

    /**
     * Obtains user's facility from database
     * Sets facility details to be displayed
     */
    public void setOrganizerDisplay(Organizer organizer) {
        Facility facility = organizer.getFacility();
        facilityNameTextView.setText(facility.getName());
        facilityPhoneNumberTextView.setText(facility.getPhoneNumber());
    }

    /**
     * Checks if the user has location permission granted
     * @return boolean representing if location has been granted
     */
    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}