package com.example.sphinxevents;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private UserManager userManager;
    private String deviceId;

    private ExpandableListView expandableListView;  // expandable list of events
    private List<String> headers;  // headers/parents/group names
    private HashMap<String, List<Event>> events;  // map each group name to list of Event objects
    private ExpandableListAdapter listAdapter;

    private ImageButton profilePicBtn;
    private ImageView profilePicDrawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (userManager.getCurrentUser() != null) {
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
        userManager = UserManager.getInstance();
        retrieveUser();

        // Initialize UI Elements
        initializeDrawer();

        expandableListView = findViewById(R.id.main_screen_expandable_listview);

        Button manageFacilityButton = findViewById(R.id.drawer_manage_facility_btn);
        manageFacilityButton.setOnClickListener(v -> {
            Intent manageFacilityIntent = new Intent(MainActivity.this, ManageFacilityActivity.class);
            startActivity(manageFacilityIntent);
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
                userManager.setCurrentUser(user);  // Set user in UserManager
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
        profilePicBtn = findViewById(R.id.profile_pic_button);
        profilePicDrawerView = findViewById(R.id.drawer_profile_pic);
        ImageButton closeDrawerButton = findViewById(R.id.close_drawer_button);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        Button manageProfileBtn = findViewById(R.id.drawer_manage_profile_btn);

        // Set profile picture button to trigger drawer
        profilePicBtn.setOnClickListener(v -> {
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
    public void updateDrawer() {
        Entrant currentUser = userManager.getCurrentUser();

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
     * Updates the expandable lists with current user data
     */
    public void updateExpandableLists() {
        Entrant currentUser = userManager.getCurrentUser();

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
        if (currentUser.getRole().equals("Organizer")) {
            Organizer organizer = (Organizer) currentUser;
            headers.add("Created Events");
            List<Event> createdEvents = new ArrayList<>();
            events.put(headers.get(2), createdEvents);
        }

        listAdapter = new EventExListAdapter(this, headers, events);
        expandableListView.setAdapter(listAdapter);

        // Clicking event in main screen -> allows user to view event details
        expandableListView.setOnChildClickListener((parent, view, groupPosition, childPosition, id) -> {
            // Code for new activity that views events goes here
            return true; // Indicating the event is handled
        });

    }

    public void updateProfilePicture() {
        Entrant currentUser = userManager.getCurrentUser();
        String customPfpPath = currentUser.getCustomPfpUri();
        if (customPfpPath.isEmpty()) {
            loadDefaultPfp();
        } else {
            Uri customImageUri = Uri.parse(currentUser.getCustomPfpUri());
            profilePicBtn.setImageURI(customImageUri);
            profilePicDrawerView.setImageURI(customImageUri);
        }
    }

    /**
     * Loads the default deterministic pfp of user
     */
    public void loadDefaultPfp() {
        Entrant currentUser = userManager.getCurrentUser();
        String userName = currentUser.getName();
        String path = currentUser.getDefaultPfpPath();

        // Check if the profile picture path is not empty
        if (path != null && !path.isEmpty()) {
            // Load the bitmap from local storage
            Bitmap profileBitmap = userManager.loadBitmapFromLocalStorage(path);

            // Set the bitmap as the image drawable for the buttons
            profilePicBtn.setImageBitmap(profileBitmap);
            profilePicDrawerView.setImageBitmap(profileBitmap);

        } else {
            // Generate a deterministic profile picture if no path is found
            Drawable textDrawable = TextDrawable.createTextDrawable(
                    this,
                    String.valueOf(userName.charAt(0)),
                    Color.WHITE,
                    140
            );
            profilePicBtn.setImageDrawable(textDrawable);
            profilePicDrawerView.setImageDrawable(textDrawable);
        }
    }

    /**
     * Listener method for user updates from UserManager
     */
    @Override
    public void onUserUpdated(Entrant updatedUser) {
        // Update UI elements based on the new currentUser data
        updateProfilePicture();
        updateDrawer();
        updateExpandableLists();
    }

    @Override
    protected void onDestroy() {
        userManager.removeUserUpdateListener(this);
        super.onDestroy();
    }
}