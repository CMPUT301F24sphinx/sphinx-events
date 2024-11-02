
package com.example.sphinxevents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static UserManager instance;
    private Entrant currentUser;
    private final List<UserUpdateListener> listeners = new ArrayList<>();

    // Private constructor to prevent direct instantiation
    private UserManager() {
    }

    // Singleton instance accessor
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    // Getter for currentUser
    public Entrant getCurrentUser() {
        return currentUser;
    }

    // Setter for currentUser, notifies all listeners of the update
    public void setCurrentUser(Entrant user) {
        this.currentUser = user;
        notifyUserUpdate();
    }

    // Adds a listener to observe user updates
    public void addUserUpdateListener(UserUpdateListener listener) {
        listeners.add(listener);
    }

    // Removes a listener to stop observing user updates
    public void removeUserUpdateListener(UserUpdateListener listener) {
        listeners.remove(listener);
    }

    // Notifies all registered listeners of a user update
    private void notifyUserUpdate() {
        for (UserUpdateListener listener : listeners) {
            listener.onUserUpdated(currentUser);
        }
    }

    // Interface to be implemented by listeners to receive update notifications
    public interface UserUpdateListener {
        void onUserUpdated(Entrant updatedUser);
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

    public Bitmap loadBitmapFromLocalStorage(String path) {
        File imgFile = new File(path);
        if (imgFile.exists()) {
            return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return null;
    }

}
