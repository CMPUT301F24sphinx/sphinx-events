package com.example.sphinxevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewQRCodeActivity extends AppCompatActivity {

    private String eventId;
    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_qr_code);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        if (intent != null ) {
            eventId = intent.getStringExtra("eventId");
        }

        qrCodeImageView = findViewById(R.id.qr_code_image_view);
        Button backButton = findViewById(R.id.back_button);

        // Back button pressed -> finish activity
        backButton.setOnClickListener(v -> {
            finish();
        });

        displayQRCode();
    }

    /**
     * Obtains QR code from database and displays it
     */
    private void displayQRCode() {
        String qrCodePath = "qr_codes/" + eventId + "QRCode.jpg";
        StorageReference qrCodeRef = FirebaseStorage.getInstance().getReference(qrCodePath);

        // Load the QR code image into the ImageView
        qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the image using Glide
            Glide.with(this)
                    .load(uri)
                    .into(qrCodeImageView);
        }).addOnFailureListener(e -> {
            // Handle any errors, e.g., if the QR code doesn't exist
            Toast.makeText(this, "Failed to load QR code", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}