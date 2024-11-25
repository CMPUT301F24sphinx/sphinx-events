package com.example.sphinxevents;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewNotificationsActivity extends AppCompatActivity {

    // declare userID, db, message and sender arrays
    private String userID;
    private DatabaseManager databaseManager;
    private UserManager userManager;
    ArrayList<String> messages;
    ArrayList<String> sender;

    /**
     * Create the Activity to view notification
     * @param savedInstanceState Bundle with the activity's previously saved state, or null.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        databaseManager = DatabaseManager.getInstance();
        userManager = UserManager.getInstance();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_notifications);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Exit activity
        ImageButton backButton = findViewById(R.id.view_notifications_back_button);
        backButton.setOnClickListener(v -> {
            finish();  // close activity when back arrow is pressed
        });

        loadNotifications();  // load the users notifications from the database

    }

    public void loadNotifications() {
        String userId = userManager.getCurrentUser().getDeviceId();

        databaseManager.getNotifications(userID, new DatabaseManager.getNotificationsCallback() {
            @Override
            public void onSuccess(ArrayList<Notification> notifications) {
                ListView simpleList = findViewById(R.id.notifs_listview);

                // Array of messages and senders
                ArrayList<String> messages = new ArrayList<>();
                ArrayList<String> sender = new ArrayList<>();

                // set the array adapter with the messages and senders
                NotificationAdapter customAdapter = new NotificationAdapter(getApplicationContext(), messages, sender);
                simpleList.setAdapter(customAdapter);
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error loading notifications", Toast.LENGTH_SHORT).show();
                finish(); // exit on fail
            }
        });
    }
}
