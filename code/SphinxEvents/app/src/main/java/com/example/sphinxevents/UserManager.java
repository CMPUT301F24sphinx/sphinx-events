
package com.example.sphinxevents;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static UserManager instance;
    private Entrant currentUser;
    private final List<UserUpdateListener> listeners = new ArrayList<>();

    // Private constructor to prevent direct instantiation
    private UserManager() {}

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
}

