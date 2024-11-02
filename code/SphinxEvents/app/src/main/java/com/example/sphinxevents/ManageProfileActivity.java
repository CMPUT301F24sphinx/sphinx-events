package com.example.sphinxevents;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManageProfileActivity extends AppCompatActivity {

    private UserManager userManager;

    private ImageView profilePicture;
    private Button changePfpBtn;
    private Button deletePfpBtn;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;
    private CheckBox organizerNotifications;
    private CheckBox adminNotifications;
    private Button cancelButton;
    private Button saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userManager = UserManager.getInstance();

        profilePicture = findViewById(R.id.manage_profile_picture);
        changePfpBtn = findViewById(R.id.manage_profile_change_pfp);
        deletePfpBtn = findViewById(R.id.manage_profile_delete_pfp);
        nameEditText = findViewById(R.id.manage_profile_name_edit_text);
        emailEditText = findViewById(R.id.manage_profile_email_edit_text);
        phoneNumberEditText = findViewById(R.id.manage_profile_phone_edit_text);
        organizerNotifications = findViewById(R.id.manage_profile_notifications_organizer);
        adminNotifications = findViewById(R.id.manage_profile_notifications_admin);
        cancelButton = findViewById(R.id.manage_profile_cancel);
        saveButton = findViewById(R.id.manage_profile_save);

        initializeListeners();
        loadUserInformation();
    }

    // TODO: implement change and delete pfp functionality
    public void initializeListeners() {

        cancelButton.setOnClickListener(v -> {
            finish();
        });

        saveButton.setOnClickListener(v -> {
            saveProfileChanges();
        });
    }

    public void loadUserInformation() {
        Entrant currentUser = userManager.getCurrentUser();

        setProfilePicture();

        nameEditText.setText(currentUser.getName());
        emailEditText.setText(currentUser.getEmail());
        phoneNumberEditText.setText(currentUser.getPhoneNumber());
    }

    public void saveProfileChanges() {
        Entrant updatedUser = userManager.getCurrentUser();

        String oldName = updatedUser.getName();
        String updatedName = nameEditText.getText().toString().trim();
        String updatedEmail = emailEditText.getText().toString().trim();
        String updatedPhone = phoneNumberEditText.getText().toString().trim();

        if (notChanged(updatedName, updatedEmail, updatedPhone)) {
            Toast.makeText(ManageProfileActivity.this, "Profile updated successfully",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (updatedName.isEmpty()) {
            nameEditText.setError("Name cannot be empty");
            return;
        } else if (updatedEmail.isEmpty()) {
            emailEditText.setError("Email cannot be empty");
            return;
        } else if (!InputValidator.isValidEmail(updatedEmail)) {
            emailEditText.setError("Invalid email address");
            return;
        } else if (!updatedPhone.isEmpty() && !InputValidator.isValidPhone(updatedPhone)) {
            phoneNumberEditText.setError("Invalid phone number");
            return;
        }

        updatedUser.setName(updatedName);
        updatedUser.setEmail(updatedEmail);
        updatedUser.setPhoneNumber(updatedPhone);

        DatabaseManager.getInstance().saveUser(updatedUser, new DatabaseManager.UserCreationCallback() {
            @Override
            public void onSuccess(String deviceId) {
                userManager.setCurrentUser(updatedUser);

                // Update users deterministic profile if name changed
                if (!oldName.equals(updatedName)) {
                    updateDefaultPfp();
                }

                Toast.makeText(ManageProfileActivity.this, "Profile updated successfully",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ManageProfileActivity.this,
                        "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        finish();
    }

    /**
     * Checks if any attribute of the user has changed
     * @param name the updated name
     * @param email the updated email
     * @param phone the updated phone
     * @return false if no changes, true if there is a change
     */
    // TODO: Notification preferences and pfp stuff
    public boolean notChanged(String name, String email, String phone) {
        Entrant currentUser = userManager.getCurrentUser();

        return currentUser.getName().equals(name) &&
                currentUser.getEmail().equals(email) &&
                currentUser.getPhoneNumber().equals(phone);
    }

    /**
     * Sets the profile picture display to users pfp
     */
    public void setProfilePicture() {
        Entrant currentUser = userManager.getCurrentUser();
        String customPfpPath = currentUser.getCustomPfpPath();
        if (customPfpPath.isEmpty()) {
            loadDefaultPfp();
        }
    }

    /**
     * loads the default profile picture form users storage to set display
     */
    public void loadDefaultPfp() {
        Entrant currentUser = userManager.getCurrentUser();
        String userName = currentUser.getName();
        String path = currentUser.getDefaultPfpPath();

        // Check if the profile picture path is not empty
        if (path != null && !path.isEmpty()) {
            // Load the bitmap from local storage
            Bitmap profileBitmap = userManager.loadBitmapFromLocalStorage(path);

            // Set the bitmap as the image drawable for the buttons
            profilePicture.setImageBitmap(profileBitmap);

        } else {
            // Generate a deterministic profile picture if no path is found
            Drawable textDrawable = TextDrawable.createTextDrawable(
                    this,
                    String.valueOf(userName.charAt(0)),
                    Color.WHITE,
                    140
            );
            profilePicture.setImageDrawable(textDrawable);
        }
    }

    public void updateDefaultPfp() {
        Entrant currentUser = UserManager.getInstance().getCurrentUser();
        String deviceId = currentUser.getDeviceId();
        String userName = currentUser.getName();



        // Generate deterministic profile picture for user
        Drawable newDefaultPfp = TextDrawable.createTextDrawable(
                this,
                String.valueOf(userName.charAt(0)),
                Color.WHITE,
                140
        );

        Bitmap defaultPfpBitmap = TextDrawable.drawableToBitmap(newDefaultPfp);
        String profilePicturePath = userManager.saveBitmapToLocalStorage(this,
                defaultPfpBitmap, deviceId);

        currentUser.setDefaultPfpPath(profilePicturePath);
        userManager.setCurrentUser(currentUser);
    }

}