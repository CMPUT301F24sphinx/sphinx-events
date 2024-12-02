/*
 * Displays results of events whose name matches the administrator's query
 * Allows administrator to click on event in list in order to view details and remove it
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

public class EventsSearchActivity extends AppCompatActivity {

    // Attributes for result list
    private TextView noResultsTextView;
    private ListView eventsList;
    private EventsAdapter eventsAdapter;

    // Attributes for updating list
    private ActivityResultLauncher<Intent> removeEventsLauncher;
    public static final int EVENTS_REMOVED = 1;
    private Event recentlyClickedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_search_events);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Sets up updating list if event is removed
        removeEventsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Called when RemoveEventsActivity returns
                    if (result.getResultCode() == EVENTS_REMOVED) {  // If event removal occurred
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
        TextView queryTextView = findViewById(R.id.search_events_text_view);
        eventsList = findViewById(R.id.events_list_view);
        noResultsTextView = findViewById(R.id.no_results_events_text_view);

        queryTextView.setText(getString(R.string.event_search_query, query));

        // Sets onClick listener for back button
        ImageButton backButton = findViewById(R.id.manage_events_back_btn);
        backButton.setOnClickListener(v -> {
            finish();  // Go back to main search screen
        });

        // Displays search results
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.searchEvents(query, new DatabaseManager.EventsSearchCallback() {
            @Override
            public void onSuccess(ArrayList<Event> events) {
                if (events.isEmpty()) {
                    setNoResultsDisplay();  // No results found
                }
                else {  // displays results
                    eventsAdapter = new EventsAdapter(EventsSearchActivity.this, events);
                    eventsList.setAdapter(eventsAdapter);

                    // Clicking event
                    eventsList.setOnItemClickListener((parent, view, position, id) -> {
                        recentlyClickedEvent = (Event) parent.getItemAtPosition(position);
                        Intent removeEventIntent = new Intent(EventsSearchActivity.this, RemoveEventsActivity.class);
                        removeEventIntent.putExtra("event", recentlyClickedEvent);
                        removeEventsLauncher.launch(removeEventIntent);
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
        eventsList.setVisibility(View.GONE);
        noResultsTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Refreshes the display after an event is removed
     */
    public void refreshDisplay() {
        eventsAdapter.remove(recentlyClickedEvent);  // Remove the clicked event

        if (eventsAdapter.getCount() == 0) {  // Check if the results list is now empty
            setNoResultsDisplay();
        }
        else {
            eventsAdapter.notifyDataSetChanged();  // Refresh the results list display
        }
    }
}
