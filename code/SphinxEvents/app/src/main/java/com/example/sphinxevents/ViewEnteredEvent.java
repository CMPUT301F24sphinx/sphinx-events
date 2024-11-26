package com.example.sphinxevents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.ArrayList;

/**
 * This will be used to view events that entrants have joined
 * Empty for now I just made it in tandem with viewCreatedEvent - Aniket
 */
public class ViewEnteredEvent extends AppCompatActivity {

    // db stuff
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseManager databaseManager;

    // Layout items
    private ImageView eventPosterLayout;
    private TextView eventNameLayout;
    private TextView eventDescLayout;
    private TextView eventDateLayout;
    private TextView eventLimitLayout;
    private TextView eventLocationReqLayout;

    // Entrants in the event waiting list
    private ArrayList<String> eventEntrants;
    private Integer eventEntrantLimit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_joined_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ID of event being viewed
        String eventCode;
        databaseManager = DatabaseManager.getInstance();

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
            eventCode = intent.getStringExtra("eventCode");
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

        // Leave the event waitlist
        eventEnterEventLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                // Set private var to event waitlist
                eventEntrants = event.getEventEntrants();
                if(eventEntrants == null){
                    eventEntrants = new ArrayList<>();
                }
                eventEntrantLimit = event.getEntrantLimit();

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
