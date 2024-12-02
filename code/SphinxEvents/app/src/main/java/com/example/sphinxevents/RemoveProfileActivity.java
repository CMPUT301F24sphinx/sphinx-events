/*
 * Displays all information of profile that was clicked by administrator in ProfilesSearchActivity
 * Allows administrator to remove user profile
 */

package com.example.sphinxevents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;

import java.io.File;

public class RemoveProfileActivity extends AppCompatActivity {

    private Entrant entrant;
    private DatabaseManager databaseManager;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_remove_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtains profile passed via intent
        Intent intent = getIntent();
        if (intent.hasExtra("profile")) {
            entrant = (Entrant) intent.getSerializableExtra("profile");
        }

        databaseManager = DatabaseManager.getInstance();

        // Obtains XML elements
        ImageButton backButton = findViewById(R.id.removing_profile_back_btn);
        TextView profileNameTextView = findViewById(R.id.profile_name_textview);
        TextView profileRoleTextView = findViewById(R.id.profile_role_textview);
        TextView profilePhoneNumberTextView = findViewById(R.id.profile_phone_number_textview);
        TextView profiledeviceIDTextView = findViewById(R.id.profile_deviceID_textview);
        ImageView profileImageView = findViewById(R.id.profileImageView);

        Button removeButton = findViewById(R.id.remove_profile_button);
        Button removePfpImageButton = findViewById(R.id.remove_profileImage_button);

        // Sets display
        profileNameTextView.setText(entrant.getName());
        profileRoleTextView.setText(entrant.getRole());
        profilePhoneNumberTextView.setText(entrant.getPhoneNumber());
        profiledeviceIDTextView.setText(entrant.getDeviceId());

        //Setting profile Image Display:
        String custom_pfp_url = entrant.getCustomPfpUrl();
        String default_pfp_path = entrant.getDefaultPfpPath();
        if(custom_pfp_url != ""){
            // Load image using Glide
            Glide.with(this)
                    .load(custom_pfp_url)
                    .into(profileImageView);
        } else {
            // Create a File object
            File imageFile = new File(default_pfp_path);
            // Load the image using Glide
            Glide.with(this)
                    .load(imageFile) // Load from file
                    .into(profileImageView);
        }

        // Set onClickListener for backButton
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Sets onClickListener for removeButton
        removeButton.setOnClickListener(v -> {
            if (entrant.getRole().equals("Organizer")) {
                removeUserFacility();
            }
            if (!entrant.getCustomPfpUrl().isEmpty()) {
                deleteCustomPfp();
            }
            removeProfile();

        });

        // Sets onClickListener for removeButton
        removePfpImageButton.setOnClickListener(v -> {
            if (custom_pfp_url != "") {
                deleteCustomPfp();
                profileImageView.setImageResource(android.R.color.transparent);
            }
            else {
                deleteDefaultPfp();
                profileImageView.setImageResource(android.R.color.transparent);
            }

        });

    }


    private void deleteDefaultPfp(){
        databaseManager.removeDefaultProfilePic(entrant.getDeviceId(), getApplicationContext());
    }

    private void deleteCustomPfp() {
        databaseManager.deleteProfilePicture(entrant.getDeviceId(), new DatabaseManager.DeleteProfilePictureCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Error removing user's profile picture", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeUserFacility() {
        databaseManager.removeFacility(entrant.getDeviceId(), new DatabaseManager.FacilityRemovalCallback() {
            @Override
            public void onSuccess(Entrant updatedUser) {
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error removing user's facility", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeProfile() {
        databaseManager.removeProfile(entrant.getDeviceId(), new DatabaseManager.ProfileRemovalCallback() {
            @Override
            public void onSuccess() {
                setResult(ProfilesSearchActivity.PROFILES_REMOVED);
                Toast.makeText(getApplicationContext(), "Profile removed!", Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Error removing profile from database", Toast.LENGTH_SHORT).show();
            }
        });
    }


}

