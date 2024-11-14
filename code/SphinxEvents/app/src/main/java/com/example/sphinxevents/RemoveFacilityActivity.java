/*
 * Displays all information of facility that was clicked by administrator in FacilitySearchActivity
 * Allows administrator to remove facility
 */

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

import com.example.sphinxevents.DatabaseManager;
import com.example.sphinxevents.Entrant;
import com.example.sphinxevents.Facility;
import com.example.sphinxevents.FacilitySearchActivity;

/**
 * Displays all information of facility that was clicked by administrator in FacilitySearchActivity
 * Allows administrator to remove facility
 */
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

        // Set onClickListener for backButton
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Sets onClickListener for removeButton
        removeButton.setOnClickListener(v -> {
            databaseManager.removeFacility(facility.getOwnerId(), new DatabaseManager.FacilityRemovalCallback() {
                @Override
                public void onSuccess(Entrant user) {
                    setResult(FacilitySearchActivity.FACILITY_REMOVED);
                    Toast.makeText(getApplicationContext(), "Facility removed!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    if (e.getMessage().equals("Failed to remove facility from user")) {
                        Toast.makeText(getApplicationContext(), "Error removing facility from user account", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error removing facility from database", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}