package com.example.sphinxevents;

import static android.widget.Toast.makeText;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.IOException;
import java.util.Objects;

public class ScanQRCode extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        if (intent != null ) {
            if("Camera".equals(intent.getAction())) {
                doQRScan();
            }
//            else if("Gallery".equals(intent.getAction())){
//                doGalleryPick();
//            }
        }
    }

//    private void doGalleryPick() {
//
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        launchSomeActivity.launch(intent);
//    }
//    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//        if (result.getResultCode() == Activity.RESULT_OK) {
//            Intent data = result.getData();
//            // do your operation from here....
//            if (data != null && data.getData() != null) {
//                Uri selectedImageUri = data.getData();
//                handleImage(selectedImageUri);
//            } else {
//                makeText(this, "Result get data failed, try again", Toast.LENGTH_SHORT).show();
//            }
//        }
//    });
//    public void handleImage(Uri selectedImageUri){
//        // code form zxing github forum translated from Kotlin to Java
//        // https://github.com/journeyapps/zxing-android-embedded/discussions/685
//
//        Bitmap selectedImageBitmap;
//        try {
//            selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        selectedImageBitmap = Bitmap.createScaledBitmap(selectedImageBitmap, 500, 700, false);
//
//        int[] intArray = new int[selectedImageBitmap.getWidth() * selectedImageBitmap.getHeight()];
//        selectedImageBitmap.getPixels(intArray, 0, selectedImageBitmap.getWidth(), 0, 0, selectedImageBitmap.getWidth(),
//                selectedImageBitmap.getHeight());
//        LuminanceSource source = new RGBLuminanceSource(selectedImageBitmap.getWidth(),
//                selectedImageBitmap.getHeight(), intArray);
//        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//
//        Reader reader = new QRCodeReader();
//        Result myresult = null;
//        try {
//            myresult = reader.decode(bitmap);
//        } catch (NotFoundException e) {
//            makeText(this, "Failed to decode image try again", Toast.LENGTH_SHORT).show();
//            finish();
//        } catch (ChecksumException e) {
//            makeText(this, "Failed to decode image try again", Toast.LENGTH_SHORT).show();
//            finish();
//        } catch (FormatException e) {
//            makeText(this, "Failed to decode image try again", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//        String decoded = myresult.getText();
//        Toast.makeText(this, decoded, Toast.LENGTH_SHORT).show();
//    }

    public void doQRScan() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to turn on flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(ScanWithCam.class);
        barcodeLauncher.launch(options);
    }

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

