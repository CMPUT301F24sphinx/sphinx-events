package com.example.sphinxevents;

import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private List<String> headers;
    private HashMap<String, List<String>> events;
    private ExpandableListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ExpandableListView expandableListView = findViewById(R.id.main_screen_expandable_listview);
        initializeExpandableLists();

        listAdapter = new ExListAdapter(this, headers, events);

        expandableListView.setAdapter(listAdapter);


        // Clicking event in main screen -> allows user to view event details
        expandableListView.setOnChildClickListener((parent, view, groupPosition, childPosition, id) -> {
            // Code for new activity goes here

            return true; // Indicating the event is handled
        });
    }


    // Just testing the expandable lists
    public void initializeExpandableLists() {
        headers = new ArrayList<>();
        events = new HashMap<>();

        headers.add("Joined Events");
        headers.add("Pending Events");

        List<String> joinedEvents = new ArrayList<>();
        List<String> pendingEvents = new ArrayList<>();

        joinedEvents.add("Joined Event #1");
        joinedEvents.add("Joined Event #2");
        joinedEvents.add("Joined Event #3");

        pendingEvents.add("Pending Event #1");
        pendingEvents.add("Pending Event #2");
        pendingEvents.add("Pending Event #3");

        events.put(headers.get(0), joinedEvents);
        events.put(headers.get(1), pendingEvents);
    }
}