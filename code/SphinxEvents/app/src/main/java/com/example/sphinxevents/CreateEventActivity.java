package com.example.sphinxevents;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

    // Manager attributes
    private DatabaseManager databaseManager;
    private UserManager userManager;
    private String organizerId;

    // UI elements
    private EditText eventNameEditText;
    private EditText eventDescriptionEditText;
    private EditText registrationDeadlineEditText;
    private EditText entrantLimitEditText;
    private CheckBox geolocationReqCheckbox;


    // Event poster attributes
    private ImageView posterImage;
    private Uri posterUri;
    private String dateString;

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
        userManager = UserManager.getInstance();
        organizerId = userManager.getCurrentUser().getDeviceId();

        // Initialize UI components
        eventNameEditText = findViewById(R.id.event_name_edit_text);
        eventDescriptionEditText = findViewById(R.id.event_description_edit_text);
        registrationDeadlineEditText = findViewById(R.id.registration_deadline_edit_text);
        posterImage = findViewById(R.id.poster_img);
        Button uploadPosterButton = findViewById(R.id.poster_upload_btn);
        entrantLimitEditText = findViewById(R.id.entrant_limit_edit_text);
        geolocationReqCheckbox = findViewById(R.id.geolocation_checkbox);
        Button cancelButton = findViewById(R.id.cancel_btn);
        Button createConfirmButton = findViewById(R.id.confirm_btn);

        // Set up date picker for registration deadline
        registrationDeadlineEditText.setOnClickListener(v -> {
            showDatePicker();
        });

        // Set up button for poster upload
        uploadPosterButton.setOnClickListener(v -> {
            selectImage();
        });

        // Set up button to create the event
        createConfirmButton.setOnClickListener(v -> {
            createEvent();
        });

        // Set up button to cancel and exit the activity
        cancelButton.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * Displays a date picker dialog and sets the selected date to the EditText.
     */
    private void showDatePicker() {
        final Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                CreateEventActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    dateString = selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDay;
                    registrationDeadlineEditText.setText(dateString);
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
     */
    private void createEvent() {
        // Obtains user input
        String eventName = eventNameEditText.getText().toString().trim();
        String eventDesc = eventDescriptionEditText.getText().toString().trim();
        String entrantLimitString = entrantLimitEditText.getText().toString().trim();
        boolean geolocationReq = geolocationReqCheckbox.isChecked();

        // Checks if all required input is entered
        if (eventName.isEmpty() || eventDesc.isEmpty() || dateString == null || posterUri == null) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!entrantLimitString.isEmpty() && Integer.parseInt(entrantLimitString) <= 0){
            Toast.makeText(this, "Entrant limit must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Converts input into proper types
        int entrantLimit;
        try {
            entrantLimit = entrantLimitString.isEmpty() ? -1 : Integer.parseInt(entrantLimitString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid entrant limit", Toast.LENGTH_SHORT).show();
            return;
        }

        Date regDate;
        try {
            regDate = new SimpleDateFormat("yyyy/MM/dd").parse(dateString);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Event object
        Organizer organizer = (Organizer) userManager.getCurrentUser();
        UserLocation facilityLocation = organizer.getFacility().getLocation();
        Event newEvent = new Event(userManager.getCurrentUser().getDeviceId(), eventName, eventDesc,
                regDate, entrantLimit, geolocationReq, new ArrayList<String>(), facilityLocation);

        databaseManager.createEvent(newEvent, new DatabaseManager.EventCreationCallback() {
            @Override
            public void onSuccess(DocumentReference eventRef) {
                // Adds Event to user and updates current user
                String eventId = eventRef.getId();
                String posterId = "EventPosters/" + eventId + ".jpg";
                newEvent.setEventId(eventId);
                newEvent.setPoster(posterId);
                Organizer currentUser = (Organizer) userManager.getCurrentUser();
                currentUser.addCreatedEvent(eventId);
                userManager.setCurrentUser(currentUser);

                // Updates user in database
                databaseManager.updateOrganizerCreatedEvents(organizerId, eventId);
                uploadPosterImage(posterId);
                Toast.makeText(getApplicationContext(), "Event created successfully", Toast.LENGTH_SHORT).show();

                startQrCodeActivity(eventId);
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
