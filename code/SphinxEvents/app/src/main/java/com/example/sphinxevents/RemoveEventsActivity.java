/*
 * Displays event information that was clicked by administrator in EventsSearchActivity
 * Allows administrator to remove event
 */

package com.example.sphinxevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        Button removeButton = findViewById(R.id.remove_event_button);

        // Sets display
        eventNameTextView.setText(event.getName());
        eventDescriptionTextView.setText(event.getDescription());
        eventLotteryDeadlineTextView.setText(event.getLotteryEndDate().toString());
        eventEntrantLimitTextView.setText(event.getEntrantLimit().toString());

        // Set onClickListener for backButton
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Sets onClickListener for removeButton
        removeButton.setOnClickListener(v -> {
            removeEvent();
        });

    }

    private void removeEvent() {
        databaseManager.removeEvent(event.getName(), new DatabaseManager.EventRemovalCallback() {
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
}
