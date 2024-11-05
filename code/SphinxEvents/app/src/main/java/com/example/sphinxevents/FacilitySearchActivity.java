package com.example.sphinxevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class FacilitySearchActivity extends AppCompatActivity {

    // Attributes for result list
    private TextView noResultsTextView;
    private ListView facilityList;
    private FacilityAdapter facilityAdapter;

    // Attributes for updating list
    private ActivityResultLauncher<Intent> removeFacilityLauncher;
    public static final int FACILITY_REMOVED = 1;
    private Facility recentlyClickedFacility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_facility_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Sets up updating list if facility is removed
        removeFacilityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Called when RemoveFacilityActivity returns
                    if (result.getResultCode() == FACILITY_REMOVED) {  // If facility removal occurred
                        refreshDisplay();  // update list display
                    }
                }
        );

        // Obtains query string passed through intent
        String query = "";
        Intent intent = getIntent();
        if (intent.hasExtra("query")) {
            query = intent.getStringExtra("query");
        }

        // Obtains XML elements
        TextView queryTextView = findViewById(R.id.search_query_text_view);
        facilityList = findViewById(R.id.facilities_list_view);
        noResultsTextView = findViewById(R.id.no_results_text_view);

        queryTextView.setText(getString(R.string.facility_search_query, query));

        // Sets onClick listener for back button
        ImageButton backButton = findViewById(R.id.search_facility_back_btn);
        backButton.setOnClickListener(v -> {
            finish();  // Go back to main search screen
        });

        // Displays search results
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.searchFacility(query, new DatabaseManager.FacilitySearchCallback() {
            @Override
            public void onSuccess(ArrayList<Facility> facilities) {
                if (facilities.isEmpty()) {
                    setNoResultsDisplay();  // No results found
                }
                else {  // displays results
                    facilityAdapter = new FacilityAdapter(FacilitySearchActivity.this, facilities);
                    facilityList.setAdapter(facilityAdapter);

                    // Clicking facility
                    facilityList.setOnItemClickListener((parent, view, position, id) -> {
                        recentlyClickedFacility = (Facility) parent.getItemAtPosition(position);
                        Intent removeFacilityIntent = new Intent(FacilitySearchActivity.this, RemoveFacilityActivity.class);
                        removeFacilityIntent.putExtra("facility", recentlyClickedFacility);
                        removeFacilityLauncher.launch(removeFacilityIntent);
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "An error occurred.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Shows proper display when no results found
     */
    public void setNoResultsDisplay() {
        facilityList.setVisibility(View.GONE);
        noResultsTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Refreshes the display after a facility is removed
     */
    public void refreshDisplay() {
        facilityAdapter.remove(recentlyClickedFacility);  // Remove the clicked facility

        if (facilityAdapter.getCount() == 0) {  // Check if the results list is now empty
            setNoResultsDisplay();
        }
        else {
            facilityAdapter.notifyDataSetChanged();  // Refresh the results list display
        }
    }
}