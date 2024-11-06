package com.example.sphinxevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewEventDetails extends AppCompatActivity {

    DatabaseManager databaseManager;
    ImageView eventPosterLayout;
    TextView eventDescLayout;
    EditText eventDateLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        String eventCode;

        Intent intent = getIntent();
        if (intent != null ) {
            eventCode = intent.getExtras().getString("eventCode");
            Toast.makeText(this, eventCode, Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "QR Scan failed in ViewEventDetails", Toast.LENGTH_SHORT).show();
            finish();
        }

        setContentView(R.layout.activity_view_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    databaseManager.getEvent(user.getDeviceId(), new DatabaseManager.facilityRetrievalCallback() {
        @Override
        public void onSuccess(Facility facility) {
            facilityNameTextView.setText(facility.getName());
            facilityLocationTextView.setText(facility.getLocation());
            facilityPhoneNumberTextView.setText(facility.getPhoneNumber());
        }
        @Override
        public void onFailure(Exception e) {
            Toast.makeText(getApplicationContext(), "Error managing facility. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
        }
    });
}
