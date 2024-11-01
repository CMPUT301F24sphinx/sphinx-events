package com.example.sphinxevents;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements UserManager.UserUpdateListener {

    private DatabaseManager databaseManager;
    private String deviceId;

    private ExpandableListView expandableListView;  // expandable list of events
    private List<String> headers;  // headers/parents/group names
    private HashMap<String, List<Event>> events;  // map each group name to list of Event objects
    private ExpandableListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (UserManager.getInstance().getCurrentUser() != null) {
                    content.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
                return false;
            }
        });

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Register as a listener for currentUser updates
        UserManager.getInstance().addUserUpdateListener(this);

        databaseManager = DatabaseManager.getInstance();
        retrieveUser();

        // Initialize UI Elements
        initializeDrawer();

        initializeExpandableLists();
        listAdapter = new EventExListAdapter(MainActivity.this, headers, events);
        expandableListView.setAdapter(listAdapter);

        // Clicking event in main screen -> allows user to view event details
        expandableListView.setOnChildClickListener((parent, view, groupPosition, childPosition, id) -> {
            // Code for new activity that views events goes here
            return true; // Indicating the event is handled
        });
    }

    /**
     * Retrieves the user from the Firestore database
     */
    public void retrieveUser() {
        deviceId = getDeviceId(this);

        databaseManager.getUser(deviceId, new DatabaseManager.UserRetrievalCallback() {
            @Override
            public void onSuccess(Entrant user) {
                UserManager.getInstance().setCurrentUser(user);  // Set user in UserManager
            }

            @Override
            public void onFailure(Exception e) {
                if (Objects.requireNonNull(e.getMessage()).contains("User does not exist")) {
                    Intent loginIntent = new Intent(MainActivity.this,
                            InitialLoginActivity.class);
                    loginIntent.putExtra("DEVICE_ID", deviceId);
                    startActivity(loginIntent);
                } else {
                    // Handle other errors, like network issues
                    Toast.makeText(MainActivity.this, "Error loading user data: " +
                            e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
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
        Button manageProfileBtn = findViewById(R.id.drawer_manage_profile_btn);

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

        // Set the Manage Profile button to trigger ManageProfile activity
        manageProfileBtn.setOnClickListener(v -> {
            Intent manageProfileIntent = new Intent(this,
                    ManageProfileActivity.class);
            startActivity(manageProfileIntent);
        });
    }

    /**
     * Updates the user information displayed in the drawer
     */
    // TODO: Update the profile picture
    public void updateDrawer() {
        Entrant currentUser = UserManager.getInstance().getCurrentUser();

        TextView userName = findViewById(R.id.drawer_user_name_display);
        TextView userEmail = findViewById(R.id.drawer_user_email_display);
        TextView userRole = findViewById(R.id.drawer_role_display);

        userName.setText(currentUser.getName());
        userEmail.setText(currentUser.getEmail());
        userRole.setText(currentUser.getRole());
    }

    /**
     * Retrieves the unique device ID.
     * @param context The context used to access system services.
     * @return The android ID of the device
     */
    public String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Initializes expandable lists with headers and event data.
     */
    public void initializeExpandableLists() {
        Entrant currentUser = UserManager.getInstance().getCurrentUser();

        expandableListView = findViewById(R.id.main_screen_expandable_listview);

        headers = new ArrayList<>();
        events = new HashMap<>();

        headers.add("Joined Events");
        headers.add("Pending Events");

        List<Event> joinedEvents = new ArrayList<>();
        List<Event> pendingEvents = new ArrayList<>();

        events.put(headers.get(0), joinedEvents);
        events.put(headers.get(1), pendingEvents);

        // Add organizer stuff if needed
        if (currentUser instanceof Organizer) {
            headers.add("Created Events");
            List<Event> createdEvents = new ArrayList<>();
            events.put(headers.get(2), createdEvents);
        }

    }

    /**
     * Listener method for user updates from UserManager
     */
    @Override
    public void onUserUpdated(Entrant updatedUser) {
        // Update UI elements based on the new currentUser data
        updateDrawer();
        initializeExpandableLists();
        // updateProfilePicture();
    }

    @Override
    protected void onDestroy() {
        UserManager.getInstance().removeUserUpdateListener(this);
        super.onDestroy();
    }
}
