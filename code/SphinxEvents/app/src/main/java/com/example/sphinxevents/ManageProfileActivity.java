/*
 * Class Name: ManageProfileActivity
 * Date: 2024-11-06
 *
 * Description:
 * ManageProfileActivity is an activity where users can view and edit their profile information.
 * It lets users update their profile picture, name, email, and phone number, with quick access to
 * options for enabling/disabling notifications. This activity also takes care of loading the user's
 * current info, validating new entries, and saving changes to the database.
 */

package com.example.sphinxevents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.io.IOException;

/**
 * ManageProfileActivity provides a simple, user-friendly interface for editing profile details.
 * Users can:
 * - View and update their profile picture (or delete it to return to a default image).
 * - Modify their name, email, and phone number.
 * - Toggle notification preferences for organizers and administrators.
 * Profile updates are validated before saving and sent to the database.
 * The activity also supports image selection for the profile picture, and loads a default
 * image with initials if no custom picture is chosen.
 */
public class ManageProfileActivity extends AppCompatActivity {

    private UserManager userManager;
    private DatabaseManager databaseManager;
    private Entrant updatedUser;

    private Uri initialProfilePicUri;
    private Uri newProfilePicUri;

    private String nameFirstChar;

    private ImageView profilePictureView;
    private Button changePfpBtn;
    private Button deletePfpBtn;
    private ActivityResultLauncher<Intent> resultLauncher;

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;

    private CheckBox orgNotificationsCheckbox;
    private CheckBox adminNotificationsCheckbox;

    private Button cancelButton;
    private Button saveButton;


    /**
     * Initializes the activity, sets up the UI, and loads the user's profile data.
     * Prepares necessary elements for managing the user's profile information.
     *
     * @param savedInstanceState The saved state of the activity, if any.
     */
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
        updatedUser = userManager.getCurrentUser().clone();
        databaseManager = DatabaseManager.getInstance();

        // Initialize variables that handle profile selection
        String profilePictureUrl = updatedUser.getProfilePictureUrl();
        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            newProfilePicUri = initialProfilePicUri = Uri.parse(profilePictureUrl);
        }

        nameFirstChar = String.valueOf(userManager.getCurrentUser().getName().charAt(0));

        profilePictureView = findViewById(R.id.manage_profile_picture);

        changePfpBtn = findViewById(R.id.manage_profile_change_pfp);
        deletePfpBtn = findViewById(R.id.manage_profile_delete_pfp);
        nameEditText = findViewById(R.id.manage_profile_name_edit_text);
        emailEditText = findViewById(R.id.manage_profile_email_edit_text);
        phoneNumberEditText = findViewById(R.id.manage_profile_phone_edit_text);
        orgNotificationsCheckbox = findViewById(R.id.manage_profile_notifications_organizer);
        adminNotificationsCheckbox = findViewById(R.id.manage_profile_notifications_admin);
        cancelButton = findViewById(R.id.manage_profile_cancel);
        saveButton = findViewById(R.id.manage_profile_save);

        registerResult();
        initializeListeners();
        loadUserInformation();
    }


    /**
     * Sets up on click listeners for the change, delete, cancel, and save buttons
     */
    public void initializeListeners() {
        changePfpBtn.setOnClickListener(v -> {
            pickImage();
        });

        deletePfpBtn.setOnClickListener(v -> {
            if (newProfilePicUri != null && updatedUser.isCustomPfp()) {
                newProfilePicUri = null;
                setProfilePictureDisplay();  // Update display to reflect deletion of picture
            }
        });

        cancelButton.setOnClickListener(v -> {
            finish();
        });

        saveButton.setOnClickListener(v -> {
            saveProfileChanges();
        });
    }

    /**
     * Initializes display to show users current information
     */
    public void loadUserInformation() {
        Entrant currentUser = userManager.getCurrentUser();

        setProfilePictureDisplay();

        // load users profile information
        nameEditText.setText(currentUser.getName());
        emailEditText.setText(currentUser.getEmail());
        phoneNumberEditText.setText(currentUser.getPhoneNumber());

        // load users notification preferences
        orgNotificationsCheckbox.setChecked(currentUser.isOrgNotificationsEnabled());
        adminNotificationsCheckbox.setChecked(currentUser.isAdminNotificationsEnabled());
    }

    /**
     * Updates the users information if any changes are made
     */
    public void saveProfileChanges() {

        String updatedName = nameEditText.getText().toString().trim();
        String updatedEmail = emailEditText.getText().toString().trim();
        String updatedPhone = phoneNumberEditText.getText().toString().trim();
        boolean updatedOrgNotificationsEnabled = orgNotificationsCheckbox.isChecked();
        boolean updatedAdminNotificationsEnabled = adminNotificationsCheckbox.isChecked();

        // If nothing changed, finish activity and return
        if (notChanged(updatedName, updatedEmail, updatedPhone, updatedOrgNotificationsEnabled,
                updatedAdminNotificationsEnabled)) {
            finish();
            return;
        }

        // Validate the new inputs and do not continue if validation fails
        if (!validateInputs(updatedName, updatedEmail, updatedPhone)) return;

        // update user information
        updatedUser.setName(updatedName);
        updatedUser.setEmail(updatedEmail);
        updatedUser.setPhoneNumber(updatedPhone);
        updatedUser.setOrgNotificationsEnabled(updatedOrgNotificationsEnabled);
        updatedUser.setAdminNotificationsEnabled(updatedAdminNotificationsEnabled);


        if (!updatedUser.isCustomPfp() && !nameFirstChar.equals(String.valueOf(updatedName.charAt(0)))) {
            generateDeterministicPfp();
        }

        // Determine if profile picture changed
        boolean profilePictureChanged = (initialProfilePicUri == null && newProfilePicUri != null) ||
                (initialProfilePicUri != null && !initialProfilePicUri.equals(newProfilePicUri));

        if (profilePictureChanged) {
                databaseManager.uploadProfilePicture(updatedUser.getDeviceId(), newProfilePicUri,
                        new DatabaseManager.UploadProfilePictureCallback() {
                            @Override
                            public void onSuccess(String url) {
                                updatedUser.setProfilePictureUrl(url);
                                saveUser();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(ManageProfileActivity.this,
                                        "Unable to upload profile picture", Toast.LENGTH_SHORT).show();
                            }
                        });
        } else {
            // No changes to profile picture
            saveUser();
        }
    }

    /**
     * Validates the name, email, and phone inputs of the user
     *
     * @param updatedName The name to validate
     * @param updatedEmail The email to validate
     * @param updatedPhone The phone to validate
     * @return True if inputs are valid, false otherwise
     */
    private boolean validateInputs(String updatedName, String updatedEmail, String updatedPhone) {
        if (updatedName.isEmpty()) {
            nameEditText.setError("Name cannot be empty");
            return false;
        } else if (updatedEmail.isEmpty()) {
            emailEditText.setError("Email cannot be empty");
            return false;
        } else if (!InputValidator.isValidEmail(updatedEmail)) {
            emailEditText.setError("Invalid email address");
            return false;
        } else if (!updatedPhone.isEmpty() && !InputValidator.isValidPhone(updatedPhone)) {
            phoneNumberEditText.setError("Invalid phone number");
            return false;
        }
        return true;
    }

    /**
     * Saves the updated user into the database. Also triggers generation of new default profile
     * picture if the users name has changed.
     */
    private void saveUser() {
        databaseManager.saveUser(updatedUser, new DatabaseManager.UserCreationCallback() {
            @Override
            public void onSuccess(String deviceId) {
                Toast.makeText(ManageProfileActivity.this, "Profile updated successfully",
                        Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ManageProfileActivity.this, "Failed to update profile: " +
                        e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Checks if profile information was updated in the inputs
     *
     * @param name Name input
     * @param email Email input
     * @param phone Phone input
     * @param orgNotificationsEnabled organizer notification preference input
     * @param adminNotificationsEnabled administrator notification preference input
     * @return True if input contents do not differ from users current information, false otherwise
     */
    public boolean notChanged(String name, String email, String phone, boolean orgNotificationsEnabled,
                              boolean adminNotificationsEnabled) {
        Entrant currentUser = userManager.getCurrentUser();

        boolean profilePicUnchanged = (initialProfilePicUri == null && newProfilePicUri == null) ||
                (initialProfilePicUri != null && initialProfilePicUri.equals(newProfilePicUri));

        return profilePicUnchanged &&
                currentUser.getName().equals(name) &&
                currentUser.getEmail().equals(email) &&
                currentUser.getPhoneNumber().equals(phone) &&
                currentUser.isOrgNotificationsEnabled() == orgNotificationsEnabled &&
                currentUser.isAdminNotificationsEnabled() == adminNotificationsEnabled;
    }

    /**
     * Sets the profile picture display
     */
    public void setProfilePictureDisplay() {
        if (newProfilePicUri == null) {  // Means no profile picture is selected at the moment
            generateDeterministicPfp();
            updatedUser.setCustomPfp(false);
            Glide.with(this)
                    .load(ProfilePictureHelper.getBitmapFromUri(newProfilePicUri))
                    .centerCrop()
                    .into(profilePictureView);
        }
        else {
            Glide.with(this)
                    .load(newProfilePicUri)
                    .centerCrop()
                    .into(profilePictureView);
        }
    }

    /**
     * Generates a deterministic profile picture based on the users name.
     */
    public void generateDeterministicPfp() {

        // Get the trimmed text from the EditText
        String nameText = nameEditText.getText().toString().trim();

        // Use first char of the name edit text if applicable
        if (!nameText.isEmpty()) {
            nameFirstChar = String.valueOf(nameText.charAt(0));
        } else {
            // Fallback to the first character of the updated user's name
            nameFirstChar = String.valueOf(updatedUser.getName().charAt(0));
        }

        // Generate deterministic profile picture for user
        Drawable newDefaultPfp = TextDrawable.createTextDrawable(
                this,
                nameFirstChar,
                Color.WHITE,
                140
        );

        Bitmap defaultPfpBitmap = ProfilePictureHelper.drawableToBitmap(newDefaultPfp, 140, 140);

        newProfilePicUri = ProfilePictureHelper.saveBitmapToTempFile(this, defaultPfpBitmap);
    }

    /**
     * Registers an activity result launcher to handle image selection,
     * updating the profile picture display if an image is selected.
     */
    private void registerResult() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Uri imageUri = result.getData() != null ? result.getData().getData() : null;
                    if (imageUri != null) {
                        newProfilePicUri = imageUri;
                        updatedUser.setCustomPfp(true);
                        setProfilePictureDisplay();
                    } else {
                        Toast.makeText(ManageProfileActivity.this, "No Image Selected",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


    /**
     * Launches an intent to pick an image from external storage
     */
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        resultLauncher.launch(intent);
    }

}