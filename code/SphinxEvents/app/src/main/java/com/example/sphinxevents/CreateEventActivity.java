package com.example.sphinxevents;

import static java.sql.Types.NULL;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Activity for creating a new event.
 * This activity allows the user to input event details, set a registration deadline,
 * upload an event poster, and save the event to Firebase.
 */
public class CreateEventActivity extends AppCompatActivity {

    private ImageView posterImage;
    private Uri posterUri;
    private String dateString;
    private DatabaseManager databaseManager;
    private String organizerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_event);

        // Set up insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseManager = DatabaseManager.getInstance();

        organizerId = UserManager.getInstance().getCurrentUser().getDeviceId();
        // Initialize UI components
        EditText eventNameText = findViewById(R.id.event_name);
        EditText eventDescText = findViewById(R.id.event_description);
        EditText regDateText = findViewById(R.id.reg_deadline);
        EditText entrantLimitText = findViewById(R.id.entrant_limit);
        CheckBox geolocationReqCheckbox = findViewById(R.id.geolocation_checkbox);
        Button cancelButton = findViewById(R.id.cancel_btn);
        Button createConfirmButton = findViewById(R.id.confirm_btn);
        Button uploadPosterButton = findViewById(R.id.poster_upload_btn);
        posterImage = findViewById(R.id.poster_img);

        // Set up date picker for registration deadline
        regDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(regDateText);
            }
        });

        // Set up button for poster upload
        uploadPosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        // Set up button to create the event
        createConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent(eventNameText, eventDescText, entrantLimitText, geolocationReqCheckbox);
            }
        });

        // Set up button to cancel and exit the activity
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Displays a date picker dialog and sets the selected date to the EditText.
     *
     * @param regDateText EditText to display selected date.
     */
    private void showDatePicker(EditText regDateText) {
        final Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                CreateEventActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    dateString = selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDay;
                    regDateText.setText(dateString);
                },
                year, month, day
        );

        // Ensure the selected date is not in the past
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    /**
     * Opens the image picker to select an image as the event poster.
     */
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 131);
    }

    /**
     * Creates a new event using the input fields and uploads the poster image if selected.
     *
     * @param eventNameText      EditText for event name.
     * @param eventDescText      EditText for event description.
     * @param entrantLimitText   EditText for entrant limit.
     * @param geolocationReqCheckbox CheckBox for geolocation requirement.
     */
    private void createEvent(EditText eventNameText, EditText eventDescText,
                             EditText entrantLimitText, CheckBox geolocationReqCheckbox) {
        String eventName = eventNameText.getText().toString().trim();
        String eventDesc = eventDescText.getText().toString().trim();
        String entrantLimitString = entrantLimitText.getText().toString().trim();
        boolean geolocationReq = geolocationReqCheckbox.isChecked();

        if (eventName.isEmpty() || eventDesc.isEmpty() || dateString == null || posterUri == null) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer entrantLimit;
        try {
            entrantLimit = entrantLimitString.isEmpty() ? NULL : Integer.valueOf(entrantLimitString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid entrant limit", Toast.LENGTH_SHORT).show();
            return;
        }
        String posterRandKey = UUID.randomUUID().toString();
        String posterId = "EventPosters/" + posterRandKey + ".jpg";
        Date regDate;
        try {
            regDate = new SimpleDateFormat("yyyy/MM/dd").parse(dateString);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> entrants = new ArrayList<String>();

        // Create a new Event object
        Event newEvent = new Event(eventName, eventDesc, posterId, regDate, entrantLimit, geolocationReq, entrants);

        databaseManager.createEvent(newEvent, new DatabaseManager.EventCreationCallback() {
            @Override
            public void onSuccess(DocumentReference eventRef) {
                Toast.makeText(getApplicationContext(), "Event created successfully", Toast.LENGTH_SHORT).show();
                databaseManager.updateOrganizerCreatedEvents(organizerId, eventRef.getId());
                uploadPosterImage(posterId);
                startQrCodeActivity(eventRef.getId());
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Event creation failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Uploads the poster image to Firebase Storage.
     *
     * @param posterId The Firebase Storage path for the poster.
     */
    private void uploadPosterImage(String posterId) {
        if (posterUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(posterId);
            storageReference.putFile(posterUri)
                    .addOnSuccessListener(taskSnapshot -> Toast.makeText(getApplicationContext(), "Poster Upload Success", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Poster Upload Fail", Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * Launches the QR code activity for the created event.
     *
     * @param eventId ID for the event, passed to the QR code activity.
     */
    private void startQrCodeActivity(String eventId) {
        Intent qrEventIntent = new Intent(CreateEventActivity.this, qrCodeActivity.class);
        qrEventIntent.putExtra("eventId", eventId);
        startActivity(qrEventIntent);
    }

    /**
     * Handles the result from the image picker and displays the selected image.
     *
     * @param requestCode The request code used to start the image picker.
     * @param resultCode  The result code from the image picker activity.
     * @param data        The data returned from the image picker, containing the selected image URI.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 131 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                posterUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(posterUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                posterImage.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
}
