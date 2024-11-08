package com.example.sphinxevents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
    private HashMap<String, List<String>> eventCodes;
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

        initializeDrawer();  // initializes drawer display and functionalities

        expandableListView = findViewById(R.id.main_screen_expandable_listview);

        // Show QR Scan choices fragment
        ImageButton scanQRCode = findViewById(R.id.scan_qr_code_button);
        scanQRCode.setOnClickListener(v -> {
            scanQRFrag();
        });
    }

    /**
     * Makes a fragment to ask user how they want to scan the QR code.
     * Opens the camera to scan a QR code if user chooses "Camera"
     * Opens the gallery to scan a QR code if user chooses "Gallery"
     */
    public void scanQRFrag() {
        AlertDialog.Builder builder  = new AlertDialog.Builder(this);
        builder.setMessage(R.string.scan_qr_code)
            .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    return;
                }
            })
// OPtional gallery scan, might do later it keeps breaking
//            .setPositiveButton(R.string.qr_use_gallery, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    Intent CamScanIntent = new Intent(MainActivity.this, ScanQRCode.class);
//                    CamScanIntent.setAction("Gallery");
//                    startActivity(CamScanIntent);
//                    return;
//                }
//            })
            .setNegativeButton(R.string.qr_camera_scan, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent CamScanIntent = new Intent(MainActivity.this, ScanQRCode.class);
                    CamScanIntent.setAction("Camera");
                    startActivity(CamScanIntent);
                    return;
                }
            }).show();
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
        Button manageFacilityBtn = findViewById(R.id.drawer_manage_facility_btn);
        Button administratorBtn = findViewById(R.id.drawer_administrator_btn);
        Button createEventBtn = findViewById(R.id.create_event_button);

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

        // Sets onClickListener for managing facility
        manageFacilityBtn.setOnClickListener(v -> {
            Intent manageFacilityIntent = new Intent(MainActivity.this, ManageFacilityActivity.class);
            startActivity(manageFacilityIntent);
        });

        // Sets OnClickListener for administrator actions
        administratorBtn.setOnClickListener(v -> {
            // TODO: Go to administrator main search screen activity
        });

        createEventBtn.setOnClickListener(v -> {
            Intent createEventIntent = new Intent(this, CreateEventActivity.class);
            createEventIntent.putExtra("DeviceID", deviceId);
            startActivity(createEventIntent);
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

        setAdminPrivileges(currentUser);
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
        eventCodes = new HashMap<>();

        headers.add("Joined Events");
        headers.add("Pending Events");

        List<Event> joinedEvents = new ArrayList<>();
        List<Event> pendingEvents = new ArrayList<>();
        ArrayList<Event> createdEvents = new ArrayList<>();

        events.put(headers.get(0), joinedEvents);
        events.put(headers.get(1), pendingEvents);

        // Add organizer stuff if needed
        if (currentUser.getRole().equals("Organizer")) {
            Organizer organizer = (Organizer) currentUser;
            headers.add("Created Events");

            databaseManager.getCreatedEvents(currentUser.getDeviceId(), new DatabaseManager.getCreatedEventsCallback() {
                @Override
                public void onSuccess(List<String> createdEventsID) {

                    eventCodes.put(headers.get(2), createdEventsID);
                    for(Integer i = 0; i < createdEventsID.size(); ++i){
                        Log.d("Aniket", eventCodes.get(headers.get(2)).get(i));
                        databaseManager.getEvent(createdEventsID.get(i), new DatabaseManager.eventRetrievalCallback() {
                            @Override
                            public void onSuccess(Event event) {
                                createdEvents.add(event);
                                Log.d("Aniket", event.getName());

                            }
                            @Override
                            public void onFailure(Exception e) {
                                Log.d("Aniket", "Cant retrieve created event with code");
                            }
                        });
                    }
                }
                @Override
                public void onFailure(Exception e) {
                }
            });
            events.put(headers.get(2), createdEvents);
        }

        listAdapter = new EventExListAdapter(this, headers, events);
        expandableListView.setAdapter(listAdapter);

        // Clicking event in main screen -> allows user to view event details
        expandableListView.setOnChildClickListener((parent, view, groupPosition, childPosition, id) -> {
            // Code for new activity that views events goes here
            Intent viewEnteredEvents = new Intent(this, ViewCreatedEvent.class);
            viewEnteredEvents.putExtra("eventCode", eventCodes.get(headers.get(groupPosition)).get(childPosition));
//            Log.d("Aniket", groupPosition + " " + childPosition + " " + eventCodes.get(headers.get(groupPosition)).get(childPosition));
//            Log.d("Aniket", eventCodes.get(headers.get(groupPosition)).get(childPosition));
            startActivity(viewEnteredEvents);
            return true; // Indicating the event is handled
        });

    }

    public void updateProfilePicture() {
        Entrant currentUser = userManager.getCurrentUser();
        String customPfpUrl = currentUser.getCustomPfpUrl();
        if (customPfpUrl != null && customPfpUrl.isEmpty()) {
            loadDefaultPfp();
        } else {
            Glide.with(this)
                    .load(customPfpUrl)
                    .centerCrop()
                    .into(profilePicBtn);
            Glide.with(this)
                    .load(customPfpUrl)
                    .centerCrop()
                    .into(profilePicDrawerView);
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
     * Allows user to access admin functionalities if they are admin
     * @param user the current user
     */
    public void setAdminPrivileges(Entrant user) {
        databaseManager.isUserAdministrator(user.getDeviceId(), new DatabaseManager.IsAdminCallback() {
            @Override
            public void onResult(boolean isAdmin) {
                if (isAdmin) {
                    // Sets administrator button as visible
                    findViewById(R.id.drawer_administrator_btn).setVisibility(View.VISIBLE);
                }
            }
        });
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