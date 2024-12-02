package com.example.sphinxevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_search), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtains XML elements
        ImageButton backButton = findViewById(R.id.admin_search_back_button);
        EditText searchBar = findViewById(R.id.searchBar);
        Button searchButton = findViewById(R.id.searchButton);
        RadioGroup filterRadioGroup = findViewById(R.id.filter_radio_group);
        Button seeAllImagesButton = findViewById(R.id.see_all_images_button);

        // Sets onClickListener for back arrow -> go back to main screen
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Sets onClickListener for search button -> determine what admin wants to search
        searchButton.setOnClickListener(v -> {
            // Obtain query
            String query = searchBar.getText().toString();

            // Check which radio button was selected
            RadioButton selectedRadioButton = findViewById(filterRadioGroup.getCheckedRadioButtonId());
            String selectedFilter = selectedRadioButton.getText().toString();

            // Go to specific activity depending on selected filter
            if (selectedFilter.equals("Facilities")) {
                Intent facilitySearchIntent = new Intent(this, FacilitySearchActivity.class);
                facilitySearchIntent.putExtra("query", query);
                startActivity(facilitySearchIntent);
            }
            else if (selectedFilter.equals("Profiles")) {
                Intent profilesSearchIntent = new Intent(this, ProfilesSearchActivity.class);
                profilesSearchIntent.putExtra("query", query);
                startActivity(profilesSearchIntent);
            }
            else {
                Intent eventsSearchIntent = new Intent(this, EventsSearchActivity.class);
                eventsSearchIntent.putExtra("query", query);
                startActivity(eventsSearchIntent);
            }
        });

        // Sets onClickListener for see all images button
        seeAllImagesButton.setOnClickListener(v -> {
            // TODO: IMPLEMENT SEE ALL IMAGES ACTIVITY (NEXT SPRINT)
        });
    }
}
