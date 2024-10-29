package com.example.sphinxevents;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseManager databaseManager;
    private String currentUserId;
    private User currentUser;

    private ExpandableListView expandableListView;  // expandable list of events
    private List<String> headers;  // headers/parents/group names
    private HashMap<String, List<Event>> events;  // map each group name to list of Event objects
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

        databaseManager = new DatabaseManager();
        currentUserId = getDeviceId(this);

        initializeDrawer();

        expandableListView = findViewById(R.id.main_screen_expandable_listview);
        initializeExpandableLists();

        listAdapter = new EventExListAdapter(this, headers, events);
        expandableListView.setAdapter(listAdapter);


        // Clicking event in main screen -> allows user to view event details
        expandableListView.setOnChildClickListener((parent, view, groupPosition, childPosition, id) -> {
            // Code for new activity that views events goes here

            return true; // Indicating the event is handled
        });
        
    }

    /**
     * Sets up the navigation drawer by initializing button listeners
     * to open and close the drawer from the right side of the screen.
     */
    public void initializeDrawer() {
        // Get drawer control elements
        ImageButton profilePicButton = findViewById(R.id.profile_pic_button);
        ImageButton closeDrawerButton = findViewById(R.id.close_drawer_button);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        // Set profile picture button to trigger drawer
        profilePicButton.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        // Set close button click listener to close drawer
        closeDrawerButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            }
        });
    }

    /**
     * Retrieves the unique device ID.
     * @param context The context used to access system services.
     * @return The android ID of the device
     */
    public String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // Just playing around with the expandable lists
    // TODO: Obtain events from database
    public void initializeExpandableLists() {
        headers = new ArrayList<>();
        events = new HashMap<>();

        headers.add("Joined Events");
        headers.add("Pending Events");

        List<Event> joinedEvents = new ArrayList<>();
        List<Event> pendingEvents = new ArrayList<>();

        /*
        joinedEvents.add(new Event("Event #1", ""));
        joinedEvents.add(new Event("Event #2", ""));
        joinedEvents.add(new Event("Event #3", ""));

        pendingEvents.add(new Event("Event #4", ""));
        pendingEvents.add(new Event("Event #5", ""));
        pendingEvents.add(new Event("Event #6", ""));
         */

        events.put(headers.get(0), joinedEvents);
        events.put(headers.get(1), pendingEvents);
    }
}