package com.example.sphinxevents;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class ViewCreatedEvent extends AppCompatActivity {

    private EventListener eventListener;
    private String eventId;
    private Event event; // The created event to display
    private DatabaseManager databaseManager;
    private Organizer organizer;

    // UI Elements
    private TextView eventNameTextView;
    private TextView eventDescriptionTextView;
    private LinearLayout numOfLotteryEntrantsLinearLayout;
    private TextView numOfLotteryEntrantsTextView;
    private TextView registrationDeadlineTextView;
    private TextView lotteryStatusTextView;
    private Button drawLotteryButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_created_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the eventID from the intent
        Intent intent = getIntent();
        if (intent != null ) {
            eventId = intent.getStringExtra("eventId");
        }

        databaseManager = DatabaseManager.getInstance();
        organizer = (Organizer) UserManager.getInstance().getCurrentUser();

        // Obtain XML elements
        eventNameTextView = findViewById(R.id.event_name_text_view);
        eventDescriptionTextView = findViewById(R.id.event_description_text_view);
        numOfLotteryEntrantsLinearLayout = findViewById(R.id.num_of_lottery_entrants_layout);
        numOfLotteryEntrantsTextView = findViewById(R.id.num_of_lottery_entrants_text_view);
        registrationDeadlineTextView = findViewById(R.id.registration_deadline_text_view);
        lotteryStatusTextView = findViewById(R.id.lottery_status_text_view);
        Button viewEventPosterButton = findViewById(R.id.view_event_poster_button);
        Button viewQRCodeButton = findViewById(R.id.view_qr_code_button);
        Button viewEntrantDataButton = findViewById(R.id.view_entrant_data_button);
        Button sendNotificationBtn = findViewById(R.id.send_notification_button);
        drawLotteryButton = findViewById(R.id.draw_lottery_button);

        // Exit activity if back arrow is pressed
        ImageButton backButton = findViewById(R.id.manage_event_back_button);
        backButton.setOnClickListener(v -> {
            finish();  // close activity when back arrow is pressed
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
                Toast.makeText(getApplicationContext(), "Error updating event display.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        eventListener.startListening();  // Start listening for changes to event

        // Sets click listener for changing event poster
        viewEventPosterButton.setOnClickListener(v -> {
            Intent viewPosterIntent = new Intent(ViewCreatedEvent.this, ViewEventPosterActivity.class);
            viewPosterIntent.putExtra("eventId", eventId);
            viewPosterIntent.putExtra("posterId", event.getPoster());
            startActivity(viewPosterIntent);
        });

        // Display QR code button pressed -> show QR code
        viewQRCodeButton.setOnClickListener(v -> {
            Intent viewQRCodeIntent = new Intent(ViewCreatedEvent.this, ViewQRCodeActivity.class);
            viewQRCodeIntent.putExtra("eventId", eventId);
            startActivity(viewQRCodeIntent);
        });

        // Allow organizer to view entrant data
        viewEntrantDataButton.setOnClickListener(v -> {
            // TODO: GO TO ACTIVITY THAT DISPLAYS USER DATA
        });

        // Allow organizer to send custom notification
        sendNotificationBtn.setOnClickListener(v -> {
            if (event != null) {
                // Create an instance of SendOrganizerNotificationFragment
                SendOrganizerNotificationFragment fragment = SendOrganizerNotificationFragment.newInstance(event);
                // Show the fragment (if in an Activity)
                fragment.show(getSupportFragmentManager(), "SendOrganizerNotificationFragment");
            } else {
                // Handle the case where the event is not available
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            }
        });

        // Allow organizer to draw a lottery
        drawLotteryButton.setOnClickListener(v -> {
            drawInputFragment();
        });


        // TODO: Move this logic to the view entrant data activity that noobray is working on
        Button viewMapButton = findViewById(R.id.view_map_button);
        viewMapButton.setOnClickListener(v -> {
            Intent viewMapIntent = new Intent(ViewCreatedEvent.this, ViewMapActivity.class);
            viewMapIntent.putExtra("event", event);
            startActivity(viewMapIntent);
        });
    }

    /**
     * Sets/Refreshes the display of the created event
     */
    private void setDisplay() {
        // Sets event name and description
        eventNameTextView.setText(event.getName());
        eventDescriptionTextView.setText(event.getDescription());
        displayRegistrationDeadline();

        // Determines display based on lottery state
        resetDisplay();  // First, reset visibility of conditional UI elements
        if (event.wasLotteryDrawn()) {  // Lottery has happened
            lotteryStatusTextView.setText(R.string.lottery_has_been_drawn);
        }
        else {
            numOfLotteryEntrantsLinearLayout.setVisibility(View.VISIBLE);
            displayEntrantCount();
            if (event.canLotteryBeDrawn()) {  // Lottery is ready to be drawn
                drawLotteryButton.setVisibility(View.VISIBLE);
                lotteryStatusTextView.setText(R.string.lottery_is_ready_to_be_drawn);
            }
            else {  // Lottery can't be drawn yet
                lotteryStatusTextView.setText(R.string.waiting_for_registration_deadline);
            }
        }
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
    private void displayEntrantCount() {
        if (event.getEntrantLimit() != null) {  // If there is an entrant limit, display it
            numOfLotteryEntrantsTextView.setText(getString(R.string.entrants_and_limit,
                    event.retrieveNumInWaitingList(), event.getEntrantLimit()));
        }
        else {
            numOfLotteryEntrantsTextView.setText(getString(R.string.entrants_and_no_limit,
                    event.retrieveNumInWaitingList()));
        }
    }

    /**
     * Resets visibility of UI elements that could be changed if the event changed
     */
    private void resetDisplay() {
        numOfLotteryEntrantsLinearLayout.setVisibility(View.GONE);
        drawLotteryButton.setVisibility(View.GONE);
    }

    /**
     * Handles when activity finished -> stop listening
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventListener.stopListening();
    }

    public void drawInputFragment(){

        EditText sampleSize = new EditText(this);
        sampleSize.setInputType(2);
        new AlertDialog.Builder(this)
                .setTitle("Draw Event Lottery")
                .setMessage("Sample number of users")
                .setView(sampleSize)
                .setPositiveButton("Draw", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String nStr = sampleSize.getText().toString().trim();

                        try {
                            Integer n = Integer.valueOf(nStr);

                            if(n <= 0){
                                Toast.makeText(ViewCreatedEvent.this, "Enter non-zero positive count", Toast.LENGTH_SHORT).show();
                            } else if (event.getEntrants().isEmpty()) {
                                Toast.makeText(ViewCreatedEvent.this, "There are no entrants for the event", Toast.LENGTH_LONG).show();
                            } else {
                                performLottery(n);
                            }
                        } catch (NumberFormatException e){
                            Toast.makeText(ViewCreatedEvent.this, "Sample count was non-numeric", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    private void performLottery(int n) {

        // Shuffles the waiting list of entrants
        ArrayList<String> tempEventEntrants = event.getEntrants();
        Collections.shuffle(tempEventEntrants);

        // new arrays for winners and losers
        ArrayList<String> tempWinners = new ArrayList<>();
        ArrayList<String> tempLosers = new ArrayList<>();

        // Ugly for-loop to assign IDs to temp arrays because java doesn't have a method to convert List<Str> to ArrayList<Str>
        for(int i = 0; i < n; i++){
            tempWinners.add(event.getEntrants().get(i));
        }
        for(int i = n; i < event.getEntrants().size(); i++){
            tempLosers.add(event.getEntrants().get(i));
        }

        // The winners put in the winner array
        // The losers stay in the entrants array
        event.setLotteryWinners(tempWinners);
        event.setEntrants(tempLosers);

        // Update the database arrays
        databaseManager.updateEventWinners(event.getEventId(), tempWinners);
        databaseManager.updateEntrants(event.getEventId(), tempLosers);

        // Send notifications to the winners and losers
        if(!tempWinners.isEmpty()) {
            NotificationsHelper.sendLotteryWinNotification(organizer.getFacility().getName(), event.getName(), tempWinners, new DatabaseManager.NotificationCreationCallback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(Exception e) {
                }
            });
        }
        if(!tempLosers.isEmpty()) {
            NotificationsHelper.sendLotteryLossNotification(organizer.getFacility().getName(), event.getName(), tempLosers, new DatabaseManager.NotificationCreationCallback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(Exception e) {
                }
            });
        }
    }

}