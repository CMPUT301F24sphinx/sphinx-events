package com.example.sphinxevents;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class ViewEventDetails extends AppCompatActivity {

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private DatabaseManager databaseManager;
    private ImageView eventPosterLayout;
    private TextView eventNameLayout;
    private TextView eventDescLayout;
    private TextView eventDateLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String eventCode;
        databaseManager = DatabaseManager.getInstance();

        eventPosterLayout = findViewById(R.id.eventPoster);
        eventNameLayout = findViewById(R.id.eventName);
        eventDescLayout = findViewById(R.id.eventDescription);
        eventDateLayout = findViewById(R.id.eventTimeRemaining);

        Intent intent = getIntent();
        if (intent != null ) {
            eventCode = intent.getExtras().getString("eventCode");
            getEvent(eventCode);
            Toast.makeText(this, eventCode, Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "QR Scan failed in ViewEventDetails", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void getEvent(String eventCode) {

        databaseManager.getEvent(eventCode, new DatabaseManager.eventRetrievalCallback() {
            @Override
            public void onSuccess(Event event) {
                Log.d("Aniket", event.getName());
                Log.d("Aniket", event.getDescription());
                Log.d("Aniket", event.getPoster());

//                SimpleDateFormat dateFormat = new SimpleDateFormat("/MM/dd/yyyy");
//                Date currDate = Calendar.getInstance().getTime();
                String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(event.getLotteryEndDate());

                String posterCode = event.getPoster();

                storageReference = FirebaseStorage.getInstance().getReference().child(posterCode);
                Glide.with(ViewEventDetails.this)
                        .load(storageReference)
                        .into(eventPosterLayout);

                eventNameLayout.setText(event.getName());
                eventDescLayout.setText(event.getDescription());
                eventDateLayout.setText(formattedDate);
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting event. Please try again.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
