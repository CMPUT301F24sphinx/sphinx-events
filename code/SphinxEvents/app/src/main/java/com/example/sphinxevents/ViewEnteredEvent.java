package com.example.sphinxevents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;

public class ViewEnteredEvent extends AppCompatActivity {

    // Manager attributes
    private EventListener eventListener;
    private UserManager userManager;
    private DatabaseManager databaseManager;
    private String eventId;
    private Event event;

    // Layout items
    private ImageView eventPosterImageView;
    private TextView eventNameTextView;
    private TextView eventDescriptionTextView;
    private TextView registrationDeadlineTextView;
    private TextView lotteryMessageTextView;
    private Button leaveWaitingListButton;
    private LinearLayout invitationOptionsLayout;
    private Button declineInvitationButton;
    private Button acceptInvitationButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entered_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtain passed eventId from intent
        Intent intent = getIntent();
        if (intent != null ) {
            eventId = intent.getStringExtra("eventId");
        }
        else {
            Toast.makeText(this, "Failed to load event details", Toast.LENGTH_SHORT).show();
            finish(); // exit activity
        }

        userManager = UserManager.getInstance();
        databaseManager = DatabaseManager.getInstance();

        // Obtain XML elements
        ImageButton backButton = findViewById(R.id.back_button);
        eventPosterImageView = findViewById(R.id.event_poster_image_view);
        eventNameTextView = findViewById(R.id.event_name_text_view);
        eventDescriptionTextView = findViewById(R.id.event_description_text_view);
        registrationDeadlineTextView = findViewById(R.id.registration_deadline_text_view);
        lotteryMessageTextView = findViewById(R.id.lottery_message_text_view);
        leaveWaitingListButton = findViewById(R.id.leave_waiting_list_button);
        invitationOptionsLayout = findViewById(R.id.invitation_options_layout);
        declineInvitationButton = findViewById(R.id.decline_invitation_button);
        acceptInvitationButton = findViewById(R.id.accept_invitation_button);

        backButton.setOnClickListener(v -> {
            finish();
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
                Toast.makeText(getApplicationContext(), "Error loading event information.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        eventListener.startListening();  // Start listening for changes to event

        leaveWaitingListButton.setOnClickListener(v -> {
            databaseManager.leaveEvent(userManager.getCurrentUser().getDeviceId(), eventId);
            Toast.makeText(getApplicationContext(), "You have left " + event.getName(), Toast.LENGTH_LONG).show();
            finish();
        });

        declineInvitationButton.setOnClickListener(v -> {
            databaseManager.cancelEvent(userManager.getCurrentUser().getDeviceId(), eventId);
            Toast.makeText(getApplicationContext(), "You have cancelled " + event.getName(), Toast.LENGTH_LONG).show();
        });

        acceptInvitationButton.setOnClickListener(v -> {
            databaseManager.confirmEvent(userManager.getCurrentUser().getDeviceId(), eventId);
            Toast.makeText(getApplicationContext(), "You have confirmed " + event.getName(), Toast.LENGTH_LONG).show();
        });
    }

    /**
     * Sets display depending on if user has won lottery or already joined
     */
    private void setDisplay() {
        Entrant user = userManager.getCurrentUser();

        // Displays event details
        displayEventPoster();
        eventNameTextView.setText(event.getName());
        eventDescriptionTextView.setText(event.getDescription());
        displayRegistrationDeadline();

        // Sets display of UI elements that changes depending on lottery state
        resetDisplay();
        if (event.getLotteryWasDrawn()) {  // Event lottery was drawn
            if (event.getLotteryWinners().contains(user.getDeviceId())) {  // User won the lottery
                lotteryMessageTextView.setText("You won the lottery! Please accept or decline the event invitation.");
                invitationOptionsLayout.setVisibility(View.VISIBLE);
            }
            else if (event.getEntrants().contains(user.getDeviceId())) {  // User lost the lottery
                lotteryMessageTextView.setText("You lost the lottery, but you can still be drawn if others decline.");
                leaveWaitingListButton.setVisibility(View.VISIBLE);
            }
            else {  // User is confirmed or cancelled
                lotteryMessageTextView.setVisibility(View.GONE);
            }
        }
        else {  // Lottery was not drawn
            lotteryMessageTextView.setText("Waiting for lottery to be drawn.");
            leaveWaitingListButton.setVisibility(View.VISIBLE);
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
     * Resets the display of UI elements that depend on lottery status
     */
    private void resetDisplay() {
        lotteryMessageTextView.setVisibility(View.VISIBLE);
        leaveWaitingListButton.setVisibility(View.GONE);
        invitationOptionsLayout.setVisibility(View.GONE);
    }

}
