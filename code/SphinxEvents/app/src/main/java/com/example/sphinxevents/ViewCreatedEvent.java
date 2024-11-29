package com.example.sphinxevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class ViewCreatedEvent extends AppCompatActivity {

    private EventListener eventListener;
    private String eventId;
    private Event event; // The created event to display

    // UI Elements
    private TextView eventNameTextView;
    private TextView eventDescriptionTextView;
    private LinearLayout numOfLotteryEntrantsLinearLayout;
    private TextView numOfLotteryEntrantsTextView;
    private TextView registrationDeadlineTextView;
    private TextView lotteryStatusTextView;
    private Button viewEventPosterButton;
    private Button drawLotteryButton;
    private LinearLayout lotteryDataTable;
    private TextView numOfLosersTextView;
    private TextView numOfWinnersTextView;
    private TextView numOfConfirmedTextView;

    /**
     * On creation of activity
     * @param savedInstanceState
     */
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

        // Obtain XML elements
        eventNameTextView = findViewById(R.id.event_name_text_view);
        eventDescriptionTextView = findViewById(R.id.event_description_text_view);
        numOfLotteryEntrantsLinearLayout = findViewById(R.id.num_of_lottery_entrants_layout);
        numOfLotteryEntrantsTextView = findViewById(R.id.num_of_lottery_entrants_text_view);
        registrationDeadlineTextView = findViewById(R.id.registration_deadline_text_view);
        lotteryStatusTextView = findViewById(R.id.lottery_status_text_view);
        viewEventPosterButton = findViewById(R.id.view_event_poster_button);
        Button viewQRCodeButton = findViewById(R.id.view_qr_code_button);
        Button viewEntrantDataButton = findViewById(R.id.view_entrant_data_button);
        Button sendMsgToEntrantsButton = findViewById(R.id.send_msg_to_entrants_button);
        drawLotteryButton = findViewById(R.id.draw_lottery_button);
        lotteryDataTable = findViewById(R.id.lottery_data_table);
        numOfLosersTextView = findViewById(R.id.num_of_losers_text_view);
        numOfWinnersTextView = findViewById(R.id.num_of_winners_text_view);
        numOfConfirmedTextView = findViewById(R.id.num_of_confirmed_text_view);

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
            viewPosterIntent.putExtra("eventPoster", event.getPoster());
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

        // Allow organizer to send custom message to entrants
        sendMsgToEntrantsButton.setOnClickListener(v -> {
            // TODO: ALLOW ORGANIZER TO SEND MSG TO ENTRANTS
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
            lotteryDataTable.setVisibility(View.VISIBLE);
            // TODO: SET THE LOSERS, WINNERS, AND CONFIRMED VALUES
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
        lotteryDataTable.setVisibility(View.GONE);
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
}
