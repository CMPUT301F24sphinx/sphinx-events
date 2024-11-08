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

    private String eventCode;
    private EditText messageTextLayout;
    DatabaseManager database;

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

        database = DatabaseManager.getInstance();


        Intent intent = getIntent();
        if (intent != null ) {
            eventCode = intent.getExtras().getString("eventCode");
        } else{
            finish();
        }

        messageTextLayout = findViewById(R.id.message_to_entrants);
        Button notifyEntrantsBtn = findViewById(R.id.notify_entrants_button);
        notifyEntrantsBtn.setOnClickListener(v -> {
            sendMessage(eventCode);
        });

        ImageButton backButton = findViewById(R.id.manage_event_back_button);
        backButton.setOnClickListener(v -> {
            finish();  // close activity when back arrow is pressed
        });
    }

    private void sendMessage(String eventCode) {
        database.getEvent(eventCode, new DatabaseManager.eventRetrievalCallback() {
            @Override
            public void onSuccess(Event event) {
                sendMessageToEntrants(event);
                ArrayList<String> entrants = event.getEventEntrants();
            }
            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    private void sendMessageToEntrants(Event event) {

        ArrayList<String> entrants = event.getEventEntrants();
        for(String entrant: entrants){

            Notification notif = new Notification(eventCode, entrant, Notification.notifType.Message);
            notif.setMessage(messageTextLayout.getText().toString());
            database.createNotification(notif, new DatabaseManager.NotificationCreationCallback() {
                @Override
                public void onSuccess(DocumentReference notifRef) {
                }
                @Override
                public void onFailure(Exception e) {
                    Log.d("Aniket", event.getName().toString());
                }
            });
        }
    }
}
