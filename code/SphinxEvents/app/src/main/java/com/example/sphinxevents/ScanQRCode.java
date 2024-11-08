package com.example.sphinxevents;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * Activity to use Camera and scan a QR code
 */
public class ScanQRCode extends AppCompatActivity {

    /**
     * On creating the QR Scan activity scan the QR code
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get info from caller intent and if it is Camera scan the qr
        Intent intent = getIntent();
        if (intent != null ) {
            if("Camera".equals(intent.getAction())) {
                doQRScan();
            }
        }
    }

    /**
     * Set options for the way the scanner looks and operates
     * Use ScanWithCam class to capture video
     */
    public void doQRScan() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to turn on flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(ScanWithCam.class);
        barcodeLauncher.launch(options);
    }

    /**
     * If the results of scan are non null start Activity to view the event
     * if null close activity and give error toast
     */
    ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            Intent LoadEventIntent = new Intent(ScanQRCode.this, ViewEventDetails.class);
            String QRResultStr = result.getContents();
            LoadEventIntent.putExtra("eventCode", QRResultStr);
            startActivity(LoadEventIntent);
            finish();
        } else {
            finish();
            makeText(this, "QR Scan failed try again", Toast.LENGTH_SHORT).show();
        }
    });
}

