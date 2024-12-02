/*
 * Class Name: RemoveEventsActivity
 * Date: 2024-12-01
 *
 *
 * Description:
 * Displays event information that was clicked by administrator in EventsSearchActivity
 * Allows administrator to remove event
 */


package com.example.sphinxevents;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Activity to handle the removal of events from the database.
 * Provides options to remove an event or delete its poster.
 */
public class RemoveEventsActivity extends AppCompatActivity {

    private Event event;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_remove_events);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtains event passed via intent
        Intent intent = getIntent();
        if (intent.hasExtra("event")) {
            event = (Event) intent.getSerializableExtra("event");
        }

        databaseManager = DatabaseManager.getInstance();

        // Obtains XML elements
        ImageButton backButton = findViewById(R.id.removing_events_back_btn);
        TextView eventNameTextView = findViewById(R.id.event_name_textview);
        TextView eventDescriptionTextView = findViewById(R.id.event_description_textview);
        TextView eventLotteryDeadlineTextView = findViewById(R.id.event_lottery_deadline_textview);
        TextView eventEntrantLimitTextView = findViewById(R.id.event_entrant_limit_textview);
        ImageView eventPosterView = findViewById(R.id.event_imageView);


        Button removeButton = findViewById(R.id.remove_event_button);
        Button removePosterButton = findViewById(R.id.remove_event_poster_button);

        // Sets display
        eventNameTextView.setText(event.getName());
        eventDescriptionTextView.setText(event.getDescription());
        eventLotteryDeadlineTextView.setText(event.getLotteryEndDate().toString());
        if (event.getEntrantLimit() == -1) {
            eventEntrantLimitTextView.setText("No Limit");
        }
        else {
            eventEntrantLimitTextView.setText(event.getEntrantLimit().toString());
        }

        // Setting imageview to Poster Image:
        String posterId = event.getPoster();
        displayEventPoster(posterId, eventPosterView);

        // Set onClickListener for backButton
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Sets onClickListener for removeButton
        removeButton.setOnClickListener(v -> {
            removeEvent();
        });

        // Sets onClickListener for removePosterButton
        //other elements:
        String eventID = event.getEventId();
        removePosterButton.setOnClickListener(v -> {
            deleteEventPoster(posterId, eventID, eventPosterView);
        });

    }

    /**
     * Deletes the event's poster from storage and updates the database.
     *
     * @param posterId   The ID of the poster in storage.
     * @param eventId    The ID of the event in the database.
     * @param posterView The ImageView displaying the poster.
     */
    private void deleteEventPoster(String posterId, String eventId, ImageView posterView) {
        // Reference to the poster in Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(posterId);

        // Deleting the file from Firebase Storage
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully then setting posterId to empty
                FirebaseFirestore.getInstance().collection("events").document(eventId)
                        .update("poster", "")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Poster deleted successfully", Toast.LENGTH_SHORT).show();
                                posterView.setImageDrawable(null);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed to reset poster...", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "Failed to delete poster", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Removes the event from the database.
     */
    private void removeEvent() {
        databaseManager.removeEvent(event.getEventId(), new DatabaseManager.EventRemovalCallback() {
            @Override
            public void onSuccess() {
                setResult(EventsSearchActivity.EVENTS_REMOVED);
                Toast.makeText(getApplicationContext(), "Event removed!", Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error removing event from database", Toast.LENGTH_SHORT).show();
            }
        });
   }

    /**
     * Displays the event's poster in an ImageView.
     *
     * @param posterId        The ID of the poster in storage.
     * @param eventPosterView The ImageView to display the poster in.
     */
    private void displayEventPoster(String posterId, ImageView eventPosterView){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(posterId);
        final long ONE_MEGABYTE = 2048 * 2048;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                eventPosterView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getApplicationContext(), "Failed to retrieve event poster", Toast.LENGTH_LONG).show();
            }
        });
    }

}
