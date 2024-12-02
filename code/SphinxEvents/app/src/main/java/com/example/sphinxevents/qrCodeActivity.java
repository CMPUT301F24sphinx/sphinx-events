/*
 * Class Name: qrCodeActivity
 * Date: 2024-11-26
 *
 *
 * Description:
 * Activity for generating and displaying a QR code based on an event's poster ID.
 * The QR code is also uploaded to Firebase Storage.
 */

package com.example.sphinxevents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;

/**
 * Activity for generating and displaying a QR code based on an event's poster ID.
 * The QR code is also uploaded to Firebase Storage.
 */
public class qrCodeActivity extends AppCompatActivity {

    private Bitmap qrBitmap;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qr_code);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView qrCodeBox = findViewById(R.id.qr_code_box); // ImageView to display QR code
        Button backToHomeButton = findViewById(R.id.back_to_home_btn); // Button to return to home

        // Retrieve poster ID passed from previous activity
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");

        // Generate and display QR code
        generateQRCode(eventId, qrCodeBox);

        // Set up back-to-home button listener
        backToHomeButton.setOnClickListener(view -> finish());
    }

    /**
     * Generates a QR code based on the provided poster ID and displays it in the specified ImageView.
     *
     * @param eventId   The ID to encode in the QR code.
     * @param qrCodeBox  The ImageView where the QR code will be displayed.
     */
    private void generateQRCode(String eventId, ImageView qrCodeBox) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            // Create QR code bit matrix with specified dimensions
            BitMatrix bitMatrix = multiFormatWriter.encode(eventId, BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            qrBitmap = barcodeEncoder.createBitmap(bitMatrix);

            // Display QR code in ImageView
            qrCodeBox.setImageBitmap(qrBitmap);

            // Upload QR code to Firebase Storage
            uploadQRCodeToStorage();

        } catch (WriterException e) {
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * Uploads the generated QR code bitmap to Firebase Storage.
     * The QR code is stored as a JPEG file in the "qr_codes" folder with the poster ID as the file name.
     */
    private void uploadQRCodeToStorage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Compress bitmap to JPEG format and retrieve bytes
        qrBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        String filePath = "qr_codes/" + eventId + ".jpg";

        StorageReference imageRef = storageReference.child(filePath);
        UploadTask uploadTask = imageRef.putBytes(data);

        // Success and failure listeners for upload
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "QR Code uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to upload QR Code", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }
}
