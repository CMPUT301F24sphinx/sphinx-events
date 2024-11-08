package com.example.sphinxevents;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class ViewCreatedEvent extends AppCompatActivity {

    private String eventCode; // ID of event
    private EditText messageTextLayout; // The textbox for the message being sent
    DatabaseManager database;

    /**
     * On creation of activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_viewcreatedevent);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get instance of database
        database = DatabaseManager.getInstance();

        // Get the eventID from intent that started this Activity
        Intent intent = getIntent();
        if (intent != null ) {
            eventCode = intent.getExtras().getString("eventCode");
        }

        // If the message entered into the textbox is nonempty send that message to entrants
        // Pass eventID to function
        messageTextLayout = findViewById(R.id.message_to_entrants);
        Button notifyEntrantsBtn = findViewById(R.id.notify_entrants_button);
        notifyEntrantsBtn.setOnClickListener(v -> {
            if(!messageTextLayout.getText().toString().equals("")){
                getEventForNotification(eventCode);
            }
        });

        // Exit activity
        ImageButton backButton = findViewById(R.id.manage_event_back_button);
        backButton.setOnClickListener(v -> {
            finish();  // close activity when back arrow is pressed
        });
    }

    /**
     * Get the Event obj sender event
     * @param eventCode
     */
    private void getEventForNotification(String eventCode) {
        database.getEvent(eventCode, new DatabaseManager.eventRetrievalCallback() {
            @Override
            public void onSuccess(Event event) {
                sendMessageToEntrants(event); // Call send message with event obj
//                ArrayList<String> entrants = event.getEventEntrants();
            }
            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    /**
     * Use database.createNotification() to upload a notification object to db
     * @param event Event object of sender event/orginazer
     */
    private void sendMessageToEntrants(Event event) {

        String eventName = event.getName(); // name of event

        // For all entrants in event make a new notification object and send to db
        ArrayList<String> entrants = event.getEventEntrants();
        for(String entrant: entrants){
            Notification notif = new Notification(eventCode, eventName, entrant, Notification.notifType.Message);
            notif.setMessage(messageTextLayout.getText().toString());
            database.createNotification(notif, new DatabaseManager.NotificationCreationCallback() {
                @Override
                public void onSuccess(DocumentReference notifRef) {
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to send message to entrants", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
