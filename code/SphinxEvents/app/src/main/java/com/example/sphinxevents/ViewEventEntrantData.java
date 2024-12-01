package com.example.sphinxevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewEventEntrantData extends AppCompatActivity {

    private UserManager userManager;
    private DatabaseManager databaseManager;
    private EventListener eventListener;

    private String eventId;
    
    private Event event;

    private ImageButton back_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_entrant_data);
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
            Toast.makeText(this, "Failed to get entrant data", Toast.LENGTH_SHORT).show();
            finish(); // exit activity
        }

        userManager = UserManager.getInstance();
        databaseManager = DatabaseManager.getInstance();

        back_button = findViewById(R.id.view_entrant_date_back_button);


        // Exit the activity with back button
        back_button.setOnClickListener(v -> {
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
                Toast.makeText(getApplicationContext(), "Error updating event. Scan QR code again.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        eventListener.startListening();  // Start listening for changes to event

    }

    private void setDisplay() {
    }
}
