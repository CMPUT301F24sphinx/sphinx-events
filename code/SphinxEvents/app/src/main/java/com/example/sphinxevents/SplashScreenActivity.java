package com.example.sphinxevents;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String deviceId = getDeviceId(this);
        DatabaseManager databaseManager = DatabaseManager.getInstance();

        databaseManager.getUser(deviceId, new DatabaseManager.UserRetrievalCallback() {
            @Override
            public void onSuccess(Entrant user) {
                // User exists, proceed to MainActivity
                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                // User does not exist, go to InitialLoginActivity for profile creation
                if (e.getMessage().contains("User does not exist")) {
                    Intent loginIntent = new Intent(SplashScreenActivity.this, InitialLoginActivity.class);
                    loginIntent.putExtra("DEVICE_ID", deviceId);
                    startActivity(loginIntent);
                } else {
                    // Handle other errors, like network issues
                    Toast.makeText(SplashScreenActivity.this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Optionally, implement a retry or fallback mechanism here
                }
                finish();
            }
        });
    }

    /**
     * Retrieves the unique device ID.
     * @param context The context used to access system services.
     * @return The android ID of the device
     */
    public String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}