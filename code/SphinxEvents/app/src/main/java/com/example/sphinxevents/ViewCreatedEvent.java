package com.example.sphinxevents;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ViewCreatedEvent extends AppCompatActivity {

    private Event currEvent;
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
            eventCode = intent.getExtras().getString("eventID");
        }

        // sets currEvent to event retrieved from db
        getCurrentEvent(eventCode);

        Button drawLotteryButton = findViewById(R.id.draw_lottery_button);
        drawLotteryButton.setOnClickListener(v ->{

            Log.d("Aniket", "DrawLotteryPressed");

            // TODO: This is supposed to be chosen by the organizer I just didn't get to making the edittext to enter a number to lottery
            // TODO: The app will break if the number of entrants is less than this n value
            int n = 3;

            Collections.shuffle(currEvent.getEventEntrants());
            List<String> sample = currEvent.getEventEntrants().subList(0, n);
        });


        // Exit activity
        ImageButton backButton = findViewById(R.id.manage_event_back_button);
        backButton.setOnClickListener(v -> {
            finish();  // close activity when back arrow is pressed
        });
    }

    private void getCurrentEvent(String eventCode){
        database.getEvent(eventCode, new DatabaseManager.eventRetrievalCallback() {
            @Override
            public void onSuccess(Event event) {
                currEvent = event;
            }
            @Override
            public void onFailure(Exception e) {
            }
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
     * @param event Event object of sender event/organizer
     */
    private void sendMessageToEntrants(Event event) {

        String eventName = event.getName(); // name of event

        // For all entrants in event make a new notification object and send to db
        ArrayList<String> entrants = event.getEventEntrants();

        Notification notification = new Notification(eventCode, eventName, Notification.notificationType.Message);
        notification.setMessage(messageTextLayout.getText().toString());
        database.createNotification(notification, entrants, new DatabaseManager.NotificationCreationCallback() {
            @Override
            public void onSuccess() {
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to send message to entrants", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
