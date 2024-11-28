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


        // Exit activity
        ImageButton backButton = findViewById(R.id.manage_event_back_button);
        backButton.setOnClickListener(v -> {
            finish();  // close activity when back arrow is pressed
        });
    }

}
