package com.example.sphinxevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RemoveFacilityActivity extends AppCompatActivity {

    private Facility facility;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_remove_facility);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtains facility passed via intent
        Intent intent = getIntent();
        if (intent.hasExtra("facility")) {
            facility = (Facility) intent.getSerializableExtra("facility");
        }

        databaseManager = DatabaseManager.getInstance();

        // Obtains XML elements
        ImageButton backButton = findViewById(R.id.removing_facility_back_btn);
        TextView facilityNameTextView = findViewById(R.id.facility_name_textview);
        TextView facilityLocationTextView = findViewById(R.id.facility_location_textview);
        TextView facilityPhoneNumberTextView = findViewById(R.id.facility_phone_number_textview);
        Button removeButton = findViewById(R.id.remove_facility_button);

        // Sets display
        facilityNameTextView.setText(facility.getName());
        facilityLocationTextView.setText(facility.getLocation());
        facilityPhoneNumberTextView.setText(facility.getPhoneNumber());

        // Sets onClickListener for removeButton
        removeButton.setOnClickListener(v -> {
            removeFacility(facility);
        });
    }

    /**
     * Removes facility from database, updates user who owns facility
     * @param facility to be removed
     */
    public void removeFacility(Facility facility) {
        databaseManager.removeFacility(facility.getOwnerId(), new DatabaseManager.FacilityRemovalCallback() {
            @Override
            public void onSuccess() {
                databaseManager.getUser(facility.getOwnerId(), new DatabaseManager.UserRetrievalCallback() {
                    @Override
                    public void onSuccess(Entrant user) {
                        // Updates user to be an Entrant
                        Entrant updatedUser = new Entrant(user.getDeviceId(), user.getName(), user.getEmail(),
                                user.getPhoneNumber(), user.getDefaultPfpPath(), user.getCustomPfpUrl(),
                                user.isOrgNotificationsEnabled(), user.isAdminNotificationsEnabled(),
                                user.getJoinedEvents(), user.getPendingEvents());
                        databaseManager.saveUser(updatedUser, new DatabaseManager.UserCreationCallback() {
                            @Override
                            public void onSuccess(String deviceId) {
                                setResult(FacilitySearchActivity.FACILITY_REMOVED);
                                Toast.makeText(getApplicationContext(), "Facility removed!", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getApplicationContext(), "Saving user failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getApplicationContext(), "Getting user failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Removing facility failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}