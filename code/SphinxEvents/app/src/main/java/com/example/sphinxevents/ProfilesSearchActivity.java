/*
 * Displays results of profiles whose name matches the administrator's query
 * Allows administrator to click on profile in list in order to view all details and remove it
 */

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

public class ProfilesSearchActivity extends AppCompatActivity {

    // Attributes for result list
    private TextView noResultsTextView;
    private ListView profilesList;
    private ProfilesAdapter profilesAdapter;

    // Attributes for updating list
    private ActivityResultLauncher<Intent> removeProfilesLauncher;
    public static final int PROFILES_REMOVED = 1;
    private Entrant recentlyClickedProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_search_profiles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Sets up updating list if profile is removed
        removeProfilesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Called when RemoveProfileActivity returns
                    if (result.getResultCode() == PROFILES_REMOVED) {  // If profile removal occurred
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
        profilesList = findViewById(R.id.profiles_list_view);
        noResultsTextView = findViewById(R.id.no_results_text_view);

        queryTextView.setText(getString(R.string.facility_search_query, query));

        // Sets onClick listener for back button
        ImageButton backButton = findViewById(R.id.manage_profiles_back_btn);
        backButton.setOnClickListener(v -> {
            finish();  // Go back to main search screen
        });

        // Displays search results
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.searchProfiles(query, new DatabaseManager.ProfilesSearchCallback() {
            @Override
            public void onSuccess(ArrayList<Entrant> users) {
                if (users.isEmpty()) {
                    setNoResultsDisplay();  // No results found
                }
                else {  // displays results
                    profilesAdapter = new ProfilesAdapter(ProfilesSearchActivity.this, users);
                    profilesList.setAdapter(profilesAdapter);

                    // Clicking facility
                    profilesList.setOnItemClickListener((parent, view, position, id) -> {
                        recentlyClickedProfile = (Entrant) parent.getItemAtPosition(position);
                        Intent removeProfileIntent = new Intent(ProfilesSearchActivity.this, RemoveProfileActivity.class);
                        removeProfileIntent.putExtra("profile", recentlyClickedProfile);
                        removeProfilesLauncher.launch(removeProfileIntent);
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
        profilesList.setVisibility(View.GONE);
        noResultsTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Refreshes the display after a facility is removed
     */
    public void refreshDisplay() {
        profilesAdapter.remove(recentlyClickedProfile);  // Remove the clicked facility

        if (profilesAdapter.getCount() == 0) {  // Check if the results list is now empty
            setNoResultsDisplay();
        }
        else {
            profilesAdapter.notifyDataSetChanged();  // Refresh the results list display
        }
    }
}