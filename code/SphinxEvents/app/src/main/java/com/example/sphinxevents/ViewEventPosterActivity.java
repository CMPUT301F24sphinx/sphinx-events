/*
 * Class Name: ViewEventPosterActivity
 * Date: 2024-11-30
 *
 * Description:
 * Allows the viewing and updating of event poster
 */

package com.example.sphinxevents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

/**
 * Fetches and displays an event poster for an Organizer. Allows user to update the poster as well.
 */
public class ViewEventPosterActivity extends AppCompatActivity {

    private String eventId;
    private String posterId;
    private ImageView eventPosterImageView;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_event_poster);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the eventID and eventPosterId from the intent
        Intent intent = getIntent();
        if (intent != null ) {
            eventId = intent.getStringExtra("eventId");
            posterId = intent.getStringExtra("posterId");
        }

        databaseManager = DatabaseManager.getInstance();

        eventPosterImageView = findViewById(R.id.event_poster_image_view);
        Button changePosterButton = findViewById(R.id.change_poster_button);
        Button backButton = findViewById(R.id.back_button);

        displayEventPoster();

        // Handles changing poster button click
        changePosterButton.setOnClickListener(v -> {
            selectImage();
        });

        // Back button clicked -> finish activity
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * Displays the event poster
     */
    private void displayEventPoster() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(posterId);
        final long ONE_MEGABYTE = 2048 * 2048;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                eventPosterImageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getApplicationContext(), "Failed to retrieve event poster", Toast.LENGTH_LONG).show();
            }
        });
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
                Uri posterUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(posterUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                databaseManager.changeEventPoster(eventId, posterId, posterUri, new DatabaseManager.changeEventPosterCallback() {
                    @Override
                    public void onSuccess() {
                        eventPosterImageView.setImageBitmap(selectedImage);
                        Toast.makeText(ViewEventPosterActivity.this, "Event poster was changed!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ViewEventPosterActivity.this, "Failed to change the poster", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Uploads the poster image to Firebase Storage.
     *
     * @param posterId The Firebase Storage path for the poster.
     */
    private void uploadPosterImage(String posterId, Uri posterUri) {
        if (posterUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(posterId);
            storageReference.putFile(posterUri)
                    .addOnSuccessListener(taskSnapshot -> Toast.makeText(getApplicationContext(), "Poster Upload Success", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Poster Upload Fail", Toast.LENGTH_SHORT).show());
        }
    }
}