package com.example.sphinxevents;

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
import android.widget.TextView;
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

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ImageView posterImage;
    public Uri posterUri;
    private String dateString;
    private DatabaseManager databaseManager;
    Button cancelButton;
    Button createConfirmButton;
    Button uploadPosterButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseManager = DatabaseManager.getInstance();


        EditText eventNameText = findViewById(R.id.event_name);
        EditText eventDescText = findViewById(R.id.event_description);
        EditText regDateText = findViewById(R.id.reg_deadline);
        EditText entrantLimitText = findViewById(R.id.entrant_limit);
        CheckBox geolocationReqCheckbox = findViewById(R.id.geolocation_checkbox);
        cancelButton = findViewById(R.id.cancel_btn);
        createConfirmButton = findViewById(R.id.confirm_btn);
        uploadPosterButton = findViewById(R.id.poster_upload_btn);
        posterImage = findViewById(R.id.poster_img);

        regDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar date = Calendar.getInstance();
                int year = date.get(Calendar.YEAR);
                int month = date.get(Calendar.MONTH);
                int day = date.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                dateString = (year + "/" + (monthOfYear + 1) + "/" + day).toString();
                                regDateText.setText(dateString);

                            }
                        },
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }

        });

        uploadPosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        createConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve the text from EditTexts
                String eventName = eventNameText.getText().toString().trim();
                String eventDesc = eventDescText.getText().toString().trim();
                String entrantLimitString = entrantLimitText.getText().toString().trim();
                boolean geolocattionReq = geolocationReqCheckbox.isChecked();

                // Check if any required fields are empty
                if (eventName.isEmpty() || eventDesc.isEmpty() || dateString == null) {
                    Toast.makeText(CreateEventActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert entrant limit to integer
                Integer entrantLimit;
                if (!entrantLimitString.isEmpty()) {
                    try {
                        entrantLimit = Integer.valueOf(entrantLimitString);
                    } catch (NumberFormatException e) {
                        Toast.makeText(CreateEventActivity.this, "Invalid entrant limit", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    entrantLimit = 0;
                }

                // Generate a random key for the poster
                String posterRandKey = UUID.randomUUID().toString();

                // Parse the date string to a Date object
                Date regDate;
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
                try {
                    regDate = dateFormatter.parse(dateString);
                } catch (ParseException e) {
                    Toast.makeText(CreateEventActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new Event object
                Event newEvent = new Event(eventName, eventDesc, posterRandKey, regDate, entrantLimit, geolocattionReq);

                // Save event to Firestore
                databaseManager.createEvent(newEvent, new DatabaseManager.EventCreationCallback() {
                    @Override
                    public void onSuccess(DocumentReference eventRef) {
                        Toast.makeText(getApplicationContext(), "Event created successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getApplicationContext(), "Event creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
                storageReference = FirebaseStorage.getInstance().getReference("images/" + posterRandKey);
                storageReference.putFile(posterUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // take all info and use as parameters for new event object





    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 131);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 131 && resultCode == RESULT_OK && data.getData() != null){
            try {
                posterUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(posterUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                posterImage.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

}