package com.example.sphinxevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class ManageFacilityActivity extends AppCompatActivity
        implements UserManager.UserUpdateListener {

    // Facility and user related attributes
    private DatabaseManager databaseManager;
    private Entrant user;

    // XML elements that change depending on user's role
    private TextView youHaveNoFacilityTextView;
    private Button addFacilityButton;
    private TextView yourFacilityTextView;
    private LinearLayout facilityNameLayout;
    private TextView facilityNameTextView;
    private LinearLayout facilityLocationLayout;
    private TextView facilityLocationTextView;
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
        UserManager userManager = UserManager.getInstance();
        user = userManager.getCurrentUser();
        userManager.addUserUpdateListener(this);
        databaseManager = DatabaseManager.getInstance();

        // Obtains relevant XML elements
        youHaveNoFacilityTextView = findViewById(R.id.you_have_no_facility_textview);
        addFacilityButton = findViewById(R.id.add_facility_button);
        yourFacilityTextView = findViewById(R.id.your_facility_textview);
        facilityNameLayout = findViewById(R.id.facility_name_layout);
        facilityNameTextView = findViewById(R.id.facility_name_textview);
        facilityLocationLayout = findViewById(R.id.facility_location_layout);
        facilityLocationTextView = findViewById(R.id.facility_location_textview);
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
            Intent addFacilityIntent = new Intent(ManageFacilityActivity.this, AddFacilityActivity.class);
            addFacilityIntent.putExtra("Context", "Add Facility");
            startActivity(addFacilityIntent);
        });

        // Clicking editFacilityButton -> start addFacilityActivity with extra context
        editFacilityButton.setOnClickListener(v -> {
            Intent editFacilityIntent = new Intent(ManageFacilityActivity.this, AddFacilityActivity.class);
            editFacilityIntent.putExtra("Context", "Edit Facility");
            startActivity(editFacilityIntent);
        });

        // Clicking removeFacilityButton -> remove facility from database and change user to Entrant
        removeFacilityButton.setOnClickListener(v -> {
            // Removes facility from database
            databaseManager.removeFacility(user.getDeviceId(), new DatabaseManager.FacilityRemovalCallback() {
                @Override
                public void onSuccess() {
                    user = new Entrant(user.getDeviceId(), user.getName(), user.getEmail(), user.getPhoneNumber(),
                            user.getDefaultPfpPath(), user.getCustomPfpUrl(), user.isOrgNotificationsEnabled(),
                            user.isAdminNotificationsEnabled(), user.getJoinedEvents(), user.getPendingEvents());
                    databaseManager.saveUser(user, new DatabaseManager.UserCreationCallback() {
                        @Override
                        public void onSuccess(String deviceId) {
                            userManager.setCurrentUser(user);
                            Toast.makeText(getApplicationContext(), "Facility removed!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            finish();
                        }
                    });
                }
                @Override
                public void onFailure(Exception e) {
                    finish();
                }
            });
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
     * @param updatedUser updated user
     */
    @Override
    public void onUserUpdated(Entrant updatedUser) {
        user = updatedUser;
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
            setOrganizerDisplay();  // sets TextView values for user's facility
        }

        // sets visibility of XML elements
        youHaveNoFacilityTextView.setVisibility(entrantVisibility);
        addFacilityButton.setVisibility(entrantVisibility);
        yourFacilityTextView.setVisibility(organizerVisibility);
        facilityNameLayout.setVisibility(organizerVisibility);
        facilityLocationLayout.setVisibility(organizerVisibility);
        facilityPhoneNumberLayout.setVisibility(organizerVisibility);
        removeEditButtonLayout.setVisibility(organizerVisibility);
    }

    /**
     * Obtains user's facility from database
     * Sets facility details to be displayed
     */
    public void setOrganizerDisplay() {
        databaseManager.getFacility(user.getDeviceId(), new DatabaseManager.facilityRetrievalCallback() {
            @Override
            public void onSuccess(Facility facility) {
                facilityNameTextView.setText(facility.getName());
                facilityLocationTextView.setText(facility.getLocation());
                facilityPhoneNumberTextView.setText(facility.getPhoneNumber());
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error managing facility. Please try again.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}