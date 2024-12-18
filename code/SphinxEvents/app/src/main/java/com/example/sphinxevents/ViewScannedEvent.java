/*
 * Class Name: ViewScannedEvent
 * Date: 2024-11_30
 *
 * Description:
 * Handles the viewing of event details when QR code is scanned
 */

package com.example.sphinxevents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;

/**
 * Handles the viewing of event details when QR code is scanned
 */
public class ViewScannedEvent extends AppCompatActivity {

    // Database and manager related attributes
    private EventListener eventListener;
    private UserManager userManager;
    private DatabaseManager databaseManager;
    private String eventId;
    private Event event;

    // UI elements
    private ImageView eventPosterImageView;
    private TextView eventNameTextView;
    private TextView eventDescriptionTextView;
    private TextView registrationDeadlineTextView;
    private TextView waitingListCountTextView;
    private Button joinWaitingListButton;

    // Warning/Indicator UI elements
    private LinearLayout alreadyJoinedEvent;
    private LinearLayout geolocationRequiredWarning;
    private LinearLayout deadlinePassedWarning;
    private LinearLayout waitingListFullWarning;
    private LinearLayout enableLocationWarning;
    private LinearLayout geolocationReqNotMetWarning;
    private LinearLayout geolocationReqMet;
    private LinearLayout ableToJoinWaitingList;

    private boolean isUserTooFar;  // boolean representing if geolocation requirements are met
    private UserLocation userLocation;  // user's location when scanning QR code if geolocation is required

    /**
     * On creation of activity
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_scanned_event);
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
            Toast.makeText(this, "QR Code Scan Failed", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Checks if the QR code wasn't removed in the database
        String qrCodePath = "qr_codes/" + eventId + ".jpg";
        StorageReference qrCodeRef = FirebaseStorage.getInstance().getReference(qrCodePath);
        // Load the QR code image into the ImageView
        qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "QR Code was removed. Unable to view event.", Toast.LENGTH_SHORT).show();
            finish();  // exit activity if QR code was removed
        });

        userManager = UserManager.getInstance();
        databaseManager = DatabaseManager.getInstance();

        // Obtain xml elements
        eventPosterImageView = findViewById(R.id.event_poster_image_view);
        eventNameTextView = findViewById(R.id.event_name_text_view);
        eventDescriptionTextView = findViewById(R.id.event_description_text_view);
        registrationDeadlineTextView = findViewById(R.id.registration_deadline_text_view);
        waitingListCountTextView = findViewById(R.id.waiting_list_count_text_view);
        waitingListFullWarning = findViewById(R.id.waiting_list_full_warning);
        Button goBackButton = findViewById(R.id.go_back_button);
        joinWaitingListButton = findViewById(R.id.join_waiting_list_button);
        alreadyJoinedEvent = findViewById(R.id.already_joined_event);
        geolocationRequiredWarning = findViewById(R.id.geolocation_required_warning);
        deadlinePassedWarning = findViewById(R.id.deadline_passed_warning);
        waitingListFullWarning = findViewById(R.id.waiting_list_full_warning);
        enableLocationWarning = findViewById(R.id.enable_location_warning);
        geolocationReqNotMetWarning = findViewById(R.id.geolocation_not_met_warning);
        geolocationReqMet = findViewById(R.id.geolocation_requirement_met);
        ableToJoinWaitingList = findViewById(R.id.able_to_join_waiting_list);

        // Exit the activity with back button
        goBackButton.setOnClickListener(v -> {
            finish();
        });

        // Join event waiting list
        joinWaitingListButton.setOnClickListener(v -> {
            joinEventWaitingList();
        });

        // Create the EventListener and start listening for updates to the event
        eventListener = new EventListener(eventId, new EventListener.EventUpdateCallback() {
            @Override
            public void onEventUpdated(Event updatedEvent) {
                event = updatedEvent;
                setDisplay();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error updating event. Scan QR code again.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        eventListener.startListening();  // Start listening for changes to event
    }

    /**
     * Sets display of event details
     */
    private void setDisplay() {
        // Displays event details
        displayEventPoster();
        eventNameTextView.setText(event.getName());
        eventDescriptionTextView.setText(event.getDescription());
        displayRegistrationDeadline();
        displayWaitListCount();

        // Displays warnings / messages and determines if user can join event
        clearWarnings();  // First, clears current warnings
        if (userCanJoinEvent()) {
            joinWaitingListButton.setVisibility(View.VISIBLE);
        }
        else {
            joinWaitingListButton.setVisibility(View.GONE);
        }
    }

    /**
     * Displays the event poster
     */
    private void displayEventPoster() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(event.getPoster());
        final long ONE_MEGABYTE = 2048 * 2048;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                eventPosterImageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getApplicationContext(), "Failed to retrieve event poster", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Displays event registration deadline
     */
    private void displayRegistrationDeadline() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' hh:mm a z");
        String formattedDate = dateFormatter.format(event.getLotteryEndDate());
        registrationDeadlineTextView.setText(formattedDate);
    }

    /**
     * Displays number of people in waiting list and indicated whether there is a limit
     */
    private void displayWaitListCount() {
        if (event.getEntrantLimit() != -1) {  // If there is an entrant limit, display it
            waitingListCountTextView.setText(getString(R.string.entrants_and_limit,
                    event.retrieveNumInWaitingList(), event.getEntrantLimit()));
        }
        else {
            waitingListCountTextView.setText(getString(R.string.entrants_and_no_limit,
                    event.retrieveNumInWaitingList()));
        }
    }

    /**
     * Clears all warnings in order to refresh display
     */
    private void clearWarnings() {
        alreadyJoinedEvent.setVisibility(View.GONE);
        geolocationRequiredWarning.setVisibility(View.GONE);
        deadlinePassedWarning.setVisibility(View.GONE);
        waitingListFullWarning.setVisibility(View.GONE);
        enableLocationWarning.setVisibility(View.GONE);
        geolocationReqNotMetWarning.setVisibility(View.GONE);
        geolocationReqMet.setVisibility(View.GONE);
        ableToJoinWaitingList.setVisibility(View.GONE);
    }

    /**
     * Determines if user can join event
     * If any checks fail, show proper warnings
     * If all checks pass, show proper message
     * @return boolean representing whether user can join event
     */
    private boolean userCanJoinEvent() {
        // Checks if user has already joined event
        if (alreadyJoined()) {
            alreadyJoinedEvent.setVisibility(View.VISIBLE);
            return false;
        }

        // Checks if lottery registration deadline has passed
        if (event.hasRegistrationDeadlinePassed()) {
            deadlinePassedWarning.setVisibility(View.VISIBLE);
            return false;
        }

        // Checks if waiting list is full
        if (event.checkIfWaitingListFull()) {
            waitingListFullWarning.setVisibility(View.VISIBLE);
            return false;
        }

        // Checks if geolocation must be enabled
        if (event.getGeolocationReq() &&
                !LocationManager.isLocationPermissionGranted(ViewScannedEvent.this)) {
            enableLocationWarning.setVisibility(View.VISIBLE);
            return false;
        }

        // If event has geolocation required
        if (event.getGeolocationReq()) {
            geolocationRequiredWarning.setVisibility(View.VISIBLE);
            // User has not enabled location permissions
            if (!LocationManager.isLocationPermissionGranted(ViewScannedEvent.this)) {
                enableLocationWarning.setVisibility(View.VISIBLE);
                return false;
            }
            // Compare user location to event facility location
            checkUserProximityToFacility();
            if (isUserTooFar) {
                geolocationReqNotMetWarning.setVisibility(View.VISIBLE);
                return false;
            }
            else {
                geolocationReqMet.setVisibility(View.VISIBLE);
            }
        }

        // All checks passed, display proper message and return true
        ableToJoinWaitingList.setVisibility(View.VISIBLE);
        return true;
    }

    /**
     * Determines if user has already joined event
     * @return boolean indicating whether user has joined event already
     */
    private boolean alreadyJoined() {
        Entrant user = userManager.getCurrentUser();
        return user.getJoinedEvents().contains(eventId) || user.getPendingEvents().contains(eventId);
    }

    /**
     * Determines if user location is too far away from facility location
     * Assigns isUserTooFar variable to correct boolean
     */
    private void checkUserProximityToFacility() {
        LocationManager.getLastLocation(ViewScannedEvent.this, new LocationManager.OnLocationReceivedListener() {
            @Override
            public void onLocationReceived(Location location) {
                // Compute distance between user's current location and event facility location
                UserLocation eventFacilityLocation = event.getFacilityLocation();
                double userDistanceToFacility = haversine(location.getLatitude(), location.getLongitude(),
                        eventFacilityLocation.getLatitude(), eventFacilityLocation.getLongitude());

                // Determines if user is too far away or not
                if (userDistanceToFacility > 300) {  // User must be within 300km of facility
                    isUserTooFar = true;
                }
                else {  // User is close enough to facility to join the event
                    isUserTooFar = false;
                    userLocation = new UserLocation(location.getLatitude(), location.getLongitude());
                }
            }

            @Override
            public void onLocationError() {
                Toast.makeText(getApplicationContext(), "Error obtaining location. Scan QR code again.", Toast.LENGTH_LONG).show();
                isUserTooFar = false;
            }
        });
    }

    /**
     * Computes the distance between two locations using the Haversine formula
     * @param lat1 latitude of first location
     * @param lon1 longitude of first location
     * @param lat2 latitude of second location
     * @param lon2 longitude of second location
     * @return distance between the two locations in kilometers
     */
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;  // returns distance converted to kilometers
    }


    /**
     * Signs user up for waitlist of event
     */
    private void joinEventWaitingList() {
        databaseManager.joinEventWaitingList(userManager.getCurrentUser().getDeviceId(), userLocation, eventId, new DatabaseManager.joinWaitingListCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "You have joined the waiting list!.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error joining event. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles when activity finished -> stop listening
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventListener.stopListening();
    }
}