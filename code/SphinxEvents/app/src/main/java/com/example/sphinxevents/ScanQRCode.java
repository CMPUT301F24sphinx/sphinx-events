package com.example.sphinxevents;

import static android.widget.Toast.makeText;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class ScanQRCode extends AppCompatActivity {

//    private ImageView eventPosterImageView;
//    private TextView eventDescriptionTextView;
//    private EditText eventTimeEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_view_event);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

//        Toast.makeText(getApplicationContext(), "Aftr scan function", Toast.LENGTH_SHORT).show();
//
//        eventPosterImageView = findViewById(R.id.eventPoster);
//        eventDescriptionTextView = findViewById(R.id.eventDescription);
//        eventTimeEditText = findViewById(R.id.eventTimeRemaining);
//        Button back = findViewById(R.id.cancel_button);
//        Button joinEvent = findViewById(R.id.add_button);
    }

    private void doQRScan() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to turn on flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(ScanWithCam.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    return;
                }
            }).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error, failed to can the QR Code try again");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            }).show();
        }
    });

//    public void scanQRFrag() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(R.string.scan_qr_code)
//                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancels the dialog.
//                        // used neutral button to have cancel on one side and rest on the other
//                        finish();
//                    }
//                })
//                .setPositiveButton(R.string.qr_use_gallery, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // START THE GAME!
//                    }
//                })
//                .setNegativeButton(R.string.qr_camera_scan, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancels the dialog.
//                        doQRScan();
//                    }
//                }).show();
//    }
}

