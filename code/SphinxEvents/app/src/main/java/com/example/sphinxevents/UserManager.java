/*
 * Class Name: UserManager
 * Date: 2024-11-06
 *
 * Description:
 * UserManager is the class that manages user-related data operations within the application.
 * This class follows a singleton pattern to ensure there is only one instance managing the
 * current user and notifying listeners about any user updates.
 */
package com.example.sphinxevents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Singleton class to manage user data and interactions with user updates
 */
public class UserManager {
    private static UserManager instance;
    private Entrant currentUser;
    private final ArrayList<UserUpdateListener> listeners = new ArrayList<>();
    private ListenerRegistration userListener;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Initializes the UserManager instance
     */
    private UserManager() {
    }

    /**
     * Gets the singleton instance of UserManager
     *
     * @return The singleton instance of UserManager
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Gets the current user.
     *
     * @return The current Entrant object.
     */
    public Entrant getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user and notifies listeners of the update.
     *
     * @param user The Entrant object to set as the current user.
     */
    public void setCurrentUser(Entrant user) {
        this.currentUser = user;
        notifyUserUpdate();
    }

    /**
     * Adds a listener to be notified when the user is updated.
     *
     * @param listener The UserUpdateListener to add.
     */
    public void addUserUpdateListener(UserUpdateListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener so that it is no longer notified when the user is updated.
     *
     * @param listener The UserUpdateListener to remove.
     */
    public void removeUserUpdateListener(UserUpdateListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all registered listeners that the current user has been updated.
     */
    private void notifyUserUpdate() {
        for (UserUpdateListener listener : listeners) {
            listener.onUserUpdated();
        }
    }

    /**
     * Starts listening for changes in the user's Firestore document.
     *
     * @param userId The user ID to listen for changes.
     */
    public void startListeningForUserChanges(String userId) {
        DocumentReference userRef = db.collection("users").document(userId);

        // Listen for real-time changes in the user's document
        userListener = userRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("UserManager", "Error listening to user document", e);
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Retrieve role from the document
                String role = documentSnapshot.getString("role");

                // Conditionally map to either Entrant or Organizer based on the role
                if ("Organizer".equals(role)) {
                    // Map to Organizer if role is "Organizer"
                    currentUser = documentSnapshot.toObject(Organizer.class);
                } else {
                    // Otherwise, map to Entrant (default case)
                    currentUser = documentSnapshot.toObject(Entrant.class);
                }

                if (currentUser != null) {
                    // Notify listeners about the updated user data
                    notifyUserUpdate();
                }
            } else {
                Log.w("UserManager", "User document not found.");
            }
        });
    }

    /**
     * Stops listening for changes to the user's Firestore document.
     */
    public void stopListeningForUserChanges() {
        if (userListener != null) {
            userListener.remove(); // Remove the listener
        }
    }

    /**
     * Saves a bitmap to local storage and returns the file path.
     *
     * @param context The application context.
     * @param bitmap  The bitmap to save.
     * @param userId  The user ID to create a unique file name.
     * @return The path of the saved image file.
     */
    public String saveBitmapToLocalStorage(Context context, Bitmap bitmap, String userId) {
        String filePath = null;
        FileOutputStream outputStream = null;

        try {
            // Create a directory for storing profile pictures
            File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profile_pics");
            if (!directory.exists()) {
                directory.mkdirs(); // Create the directory if it doesn't exist
            }

            // Create a unique file name for the bitmap using userId
            File file = new File(directory, userId + "_profile_picture.png");
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // Save the bitmap as PNG

            filePath = file.getAbsolutePath(); // Get the absolute path of the saved file
        } catch (IOException e) {
            Log.e("UserManager", "Error saving profile picture: " + e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.e("UserManager", "Error closing output stream: " + e.getMessage());
                }
            }
        }

        return filePath; // Return the path of the saved image file
    }

    /**
     * Loads a bitmap from local storage using a file path.
     *
     * @param path The path of the image file.
     * @return The Bitmap object if the file exists, otherwise null.
     */
    public Bitmap loadBitmapFromLocalStorage(String path) {
        File imgFile = new File(path);
        if (imgFile.exists()) {
            return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return null;
    }

    /**
     * Listener interface for receiving user update notifications.
     */
    public interface UserUpdateListener {
        /**
         * Called when the user data is updated.
         */
        void onUserUpdated();
    }
}

