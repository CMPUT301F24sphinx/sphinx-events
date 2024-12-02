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

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

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
     * Listener interface for receiving user update notifications.
     */
    public interface UserUpdateListener {
        /**
         * Called when the user data is updated.
         */
        void onUserUpdated();
    }
}

