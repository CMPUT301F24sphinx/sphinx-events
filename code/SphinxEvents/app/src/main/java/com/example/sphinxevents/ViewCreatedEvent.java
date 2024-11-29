package com.example.sphinxevents;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

    private Event event; // The created event to display

    // XML elements that may be changed
    private TextView eventNameTextView;
    private TextView eventDescriptionTextView ;
    private TextView numOfLotteryEntrantsLabel;
    private TextView numOfLotteryEntrantsTextView;
    private TextView registrationDeadlineTextView;
    private TextView lotteryStatusTextView;
    private Button drawLotteryButton;

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

        // Get the event show details of from the intent
        Intent intent = getIntent();
        if (intent != null ) {
            event = (Event) intent.getSerializableExtra("eventToView");
        }

        // Obtain XML elements
        eventNameTextView = findViewById(R.id.event_name_text_view);
        eventDescriptionTextView = findViewById(R.id.event_description_text_view);
        numOfLotteryEntrantsLabel = findViewById(R.id.num_of_lottery_entrants_label);
        numOfLotteryEntrantsTextView = findViewById(R.id.num_of_lottery_entrants_text_view);
        registrationDeadlineTextView = findViewById(R.id.registration_deadline_text_view);
        lotteryStatusTextView = findViewById(R.id.lottery_status_text_view);
        Button changeEventPosterButton = findViewById(R.id.change_event_poster_button);
        Button viewQRCodeButton = findViewById(R.id.view_qr_code_button);
        Button viewEntrantDataButton = findViewById(R.id.view_entrant_data_button);
        Button sendMsgToEntrantsButton = findViewById(R.id.send_msg_to_entrants_button);
        drawLotteryButton = findViewById(R.id.draw_lottery_button);

        setDisplay();

        // Exit activity if back arrow is pressed
        ImageButton backButton = findViewById(R.id.manage_event_back_button);
        backButton.setOnClickListener(v -> {
            finish();  // close activity when back arrow is pressed
        });

        // Allow organizer to edit event details
        changeEventPosterButton.setOnClickListener(v -> {
            // TODO: Navigate to new activity that allows organizer to edit event
        });

        // Display QR code button pressed -> show QR code
        viewQRCodeButton.setOnClickListener(v -> {
            // TODO: Display QR code
        });

        // Allow organizer to view entrant data
        viewEntrantDataButton.setOnClickListener(v -> {
            // TODO: Go to new activity that displays user data
        });

        // Allow organizer to send custom message to entrants
        sendMsgToEntrantsButton.setOnClickListener(v -> {
            // TODO: Display fragment that allows organizer to send message
        });

        // Allow organizer to draw a lottery
        drawLotteryButton.setOnClickListener(v -> {
            drawInputFragment();
        });
    }

    /**
     * Sets/Refreshes the display of the created event
     * Called whenever something changes with the event
     */
    private void setDisplay() {
        // Sets display of common XML elements
        eventNameTextView.setText(event.getName());
        eventDescriptionTextView.setText(event.getDescription());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        registrationDeadlineTextView.setText(sdf.format(event.getLotteryEndDate()));

        // Determines display based on lottery state
        if (event.wasLotteryDrawn()) {
            drawLotteryButton.setVisibility(View.GONE);
            numOfLotteryEntrantsLabel.setText("");
            lotteryStatusTextView.setText("Lottery has been drawn!");
        }
        else if (event.canLotteryBeDrawn()) {  // Lottery is ready to be drawn
            drawLotteryButton.setVisibility(View.VISIBLE);
            lotteryStatusTextView.setText("Lottery is ready to be drawn!");
        }
        else {
            drawLotteryButton.setVisibility(View.GONE);
            lotteryStatusTextView.setText("Waiting for registration deadline");
        }
    }

    public void drawInputFragment(){

        final EditText sampleSize = new EditText(this);
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
                            } else {
                                //TODO: call lottery draw
                                Log.d("Aniket", String.valueOf(n));
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

}