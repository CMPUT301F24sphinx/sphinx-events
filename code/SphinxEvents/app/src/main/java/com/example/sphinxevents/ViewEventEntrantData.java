package com.example.sphinxevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewEventEntrantData extends AppCompatActivity {

    private DatabaseManager databaseManager;
    private EventListener eventListener;

    private ExpandableListView expandableListView;  // expandable list of events
    private ExpandableListAdapter listAdapter;
    private List<String> headers;  // headers/parents/group names
    private HashMap<String, List<Entrant>> entrantList;  // map each group name to list of Event objects
    private String eventId;
    private Event currEvent;

    private ImageButton back_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_entrant_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtain passed eventId from ScanQRCode via intent
        Intent intent = getIntent();
        if (intent != null ) {
            eventId = intent.getStringExtra("eventId");
        }
        else {
            Toast.makeText(this, "Failed to get entrant data", Toast.LENGTH_SHORT).show();
            finish(); // exit activity
        }

        databaseManager = DatabaseManager.getInstance();

        back_button = findViewById(R.id.view_entrant_date_back_button);
        expandableListView = findViewById(R.id.entrant_data_expandable_listview);

        // Exit the activity with back button
        back_button.setOnClickListener(v -> {
            finish();
        });

        // Create the EventListener and start listening for updates to the event
        eventListener = new EventListener(eventId, new EventListener.EventUpdateCallback() {
            @Override
            public void onEventUpdated(Event updatedEvent) {
                currEvent = updatedEvent;
                updateExpandableLists();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error updating event. Scan QR code again.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        eventListener.startListening();  // Start listening for changes to event

        Button viewMapButton = findViewById(R.id.view_map_button);
        viewMapButton.setOnClickListener(v -> {
            Intent viewMapIntent = new Intent(ViewEventEntrantData.this, ViewMapActivity.class);
            viewMapIntent.putExtra("event", currEvent);
            startActivity(viewMapIntent);
        });
    }

    public void updateExpandableLists() {
        headers = new ArrayList<>();
        entrantList = new HashMap<>();

        headers.add(getString(R.string.confirmed_entrants, currEvent.getConfirmed().size()));
        headers.add(getString(R.string.entrants_invited_to_apply, currEvent.getLotteryWinners().size()));
        headers.add(getString(R.string.cancelled_entrants, currEvent.getCancelled().size()));
        headers.add(getString(R.string.entrants, currEvent.getEntrants().size()));

        databaseManager.retrieveEntrantList(currEvent.getConfirmed(), new DatabaseManager.retrieveEntrantListCallback() {
            @Override
            public void onSuccess(List<Entrant> entrants) {
                entrantList.put(headers.get(0), entrants);
            }
            @Override
            public void onFailure(Exception e) {
            }
        });

        databaseManager.retrieveEntrantList(currEvent.getLotteryWinners(), new DatabaseManager.retrieveEntrantListCallback() {
            @Override
            public void onSuccess(List<Entrant> entrants) {
                entrantList.put(headers.get(1), entrants);
            }
            @Override
            public void onFailure(Exception e) {
            }
        });

        databaseManager.retrieveEntrantList(currEvent.getCancelled(), new DatabaseManager.retrieveEntrantListCallback() {
            @Override
            public void onSuccess(List<Entrant> entrants) {
                entrantList.put(headers.get(2), entrants);
            }
            @Override
            public void onFailure(Exception e) {
            }
        });

        databaseManager.retrieveEntrantList(currEvent.getEntrants(), new DatabaseManager.retrieveEntrantListCallback() {
            @Override
            public void onSuccess(List<Entrant> entrants) {
                entrantList.put(headers.get(3), entrants);
            }
            @Override
            public void onFailure(Exception e) {
            }
        });

        listAdapter = new EntrantExListAdapter(ViewEventEntrantData.this, headers, entrantList);
        expandableListView.setAdapter(listAdapter);
        expandableListView.setOnChildClickListener((parent, view, groupPosition, childPosition, id) -> {
            Entrant clickedEntrant = (Entrant) listAdapter.getChild(groupPosition, childPosition);
            if (groupPosition == 1) {  // Clicked on a lottery winner
                // Create and show the dialog fragment
                CancelEntrantDialogFragment dialogFragment = CancelEntrantDialogFragment.newInstance(currEvent, clickedEntrant);
                dialogFragment.show(getSupportFragmentManager(), "CancelEntrantDialog");
            }
            return true;
        });
    }
}
