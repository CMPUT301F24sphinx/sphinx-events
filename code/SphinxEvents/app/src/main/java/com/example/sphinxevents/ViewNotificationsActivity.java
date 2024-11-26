package com.example.sphinxevents;

import static android.widget.Toast.makeText;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ViewNotificationsActivity extends AppCompatActivity {

    private DatabaseManager databaseManager;
    private UserManager userManager;

    /**
     * Create the Activity to view notification
     * @param savedInstanceState Bundle with the activity's previously saved state, or null.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_notifications);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize managers
        databaseManager = DatabaseManager.getInstance();
        userManager = UserManager.getInstance();

        // Exit activity
        ImageButton backButton = findViewById(R.id.view_notifications_back_button);
        backButton.setOnClickListener(v -> {
            finish();  // close activity when back arrow is pressed
        });

        loadNotifications();  // load the users notifications from the database
    }

    /**
     * Loads the notifications for the current user
     */
    public void loadNotifications() {
        String userID = userManager.getCurrentUser().getDeviceId();

        databaseManager.getNotifications(userID, new DatabaseManager.getNotificationsCallback() {
            @Override
            public void onSuccess(ArrayList<Notification> notifications) {
                ListView simpleList = findViewById(R.id.notifications_listview);
                // Get the no notifications TextView
                TextView noNotificationsTextView = findViewById(R.id.no_notifications_msg);

                // If there are notifications, display them in the ListView
                if (notifications != null && !notifications.isEmpty()) {
                    simpleList.setVisibility(View.VISIBLE); // Show the ListView
                    noNotificationsTextView.setVisibility(View.GONE); // Hide the "No Notifications" message

                    // Set the adapter for the ListView with notifications
                    NotificationsAdapter customAdapter = new NotificationsAdapter(getApplicationContext(), notifications);
                    simpleList.setAdapter(customAdapter);
                } else {
                    // If there are no notifications, show the "No Notifications" message
                    simpleList.setVisibility(View.GONE); // Hide the ListView
                    noNotificationsTextView.setVisibility(View.VISIBLE); // Show the "No Notifications" message
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error loading notifications", Toast.LENGTH_SHORT).show();
                finish(); // Exit on failure
            }
        });
    }
}
