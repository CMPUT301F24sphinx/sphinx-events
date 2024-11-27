package com.example.sphinxevents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class ViewEventDetails extends AppCompatActivity {

    // db stuff
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseManager databaseManager;
    private UserManager userManager;

    // Layout items
    private ImageView eventPosterLayout;
    private TextView eventNameLayout;
    private TextView eventDescLayout;
    private TextView eventDateLayout;
    private TextView eventLimitLayout;
    private TextView eventLocationReqLayout;

    /**
     * On creation of activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ID of event being viewed
        String eventCode;
        databaseManager = DatabaseManager.getInstance();
        userManager = UserManager.getInstance();

        // Connect layout stuff to xml views
        eventPosterLayout = findViewById(R.id.eventPoster);
        eventNameLayout = findViewById(R.id.eventName);
        eventDescLayout = findViewById(R.id.eventDescription);
        eventDateLayout = findViewById(R.id.eventTimeRemaining);
        eventLimitLayout = findViewById(R.id.eventLimit);
        eventLocationReqLayout = findViewById(R.id.eventLocationReq);
        Button eventGoBackLayout = findViewById(R.id.cancel_event_button);
        Button eventEnterEventLayout = findViewById(R.id.add_event_button);

        // Extract passed event code from previous activity and then query that Event from db
        Intent intent = getIntent();
        if (intent != null ) {
            eventCode = intent.getStringExtra("eventId");
            getEvent(eventCode);
        } else{
            Toast.makeText(this, "QR Scan failed in ViewEventDetails", Toast.LENGTH_SHORT).show();
            finish(); // exit activity if failed
        }

        // Exit the activity with back button
        eventGoBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Check if user can join event
        eventEnterEventLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinEventCheck(getIntent().getExtras().getString("eventCode"));
            }
        });
    }

    /**
     * Check if user can enter the event
     * This functionality isn't fully implemented because it's not required by Part3 due date
     * @param eventCode ID of the event to join
     */
    public void joinEventCheck(String eventCode){
        databaseManager.getEvent(eventCode, new DatabaseManager.eventRetrievalCallback() {
            @Override
            public void onSuccess(Event event) {

                String currUser = userManager.getCurrentUser().getDeviceId();
                ArrayList<String> eventEntrants = event.getEventEntrants();

                if(eventEntrants == null){
                    eventEntrants = new ArrayList<>();
                }

                if(eventEntrants != null){
                    for (int i = 0; i < eventEntrants.size(); i++) {
                        if (currUser.equals(eventEntrants.get(i))) {
                            Toast.makeText(getApplicationContext(), "You have already joined this event!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

                if(event.getEntrantLimit() == null){
                    // no limit JOIN
                    databaseManager.joinEvent(currUser, eventCode);
                } else if(eventEntrants.size() < event.getEntrantLimit()){
                    // Event not full and has limit
                    databaseManager.joinEvent(currUser, eventCode);
                } else if (eventEntrants.size() >= event.getEntrantLimit()){
                    // Event full cant join
                    Toast.makeText(getApplicationContext(), "The event is full cannot join", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting event. Please try again.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Get event obj with eventCode eventID
     * Sets the activity layout text and image to event data queried
     * @param eventCode eventID of event we want
     */
    public void getEvent(String eventCode) {

        databaseManager.getEvent(eventCode, new DatabaseManager.eventRetrievalCallback() {
            @Override
            public void onSuccess(Event event) {

                // Get information of event
                String posterCode = event.getPoster();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:m:s a z");
                String formattedDate = dateFormatter.format(event.getLotteryEndDate());

                // Set layout items to information gotten above
                eventNameLayout.setText(event.getName());
                eventDescLayout.setText(event.getDescription());
                eventDateLayout.setText(formattedDate);
                eventLimitLayout.setText(event.getEntrantLimit() != null ? event.getEntrantLimit().toString() : "0");

                // If location is required change the text otherwise the default says no location required
                // when joining events is fully implements this will have to check users locaion or else fail
                if(event.getGeolocationReq() == true){
                    eventLocationReqLayout.setText("Your location has to match the Event's Facility Location");
                }

                // Get poster from database using filepath of poster
                storageReference = FirebaseStorage.getInstance().getReference().child(posterCode);
                final long ONE_MEGABYTE = 2048 * 2048;
                storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        eventPosterLayout.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Toast.makeText(getApplicationContext(), "Failed to retrieve event poster", Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting event. Please try again.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
