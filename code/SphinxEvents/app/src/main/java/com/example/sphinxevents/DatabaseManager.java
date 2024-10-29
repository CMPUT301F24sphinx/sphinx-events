
package com.example.sphinxevents;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private final FirebaseFirestore database;

    /**
     * Initializes a new DatabaseManager instance with a Firestore database connection.
     */
    public DatabaseManager() {
        database = FirebaseFirestore.getInstance();
    }

    /**
     * Callback interface for handling the result of an event creation operation.
     */
    public interface EventCreationCallback {
        /**
         * Called when the event is successfully created.
         * @param eventRef Reference to the created event document in Firestore.
         */
        void onSuccess(DocumentReference eventRef);

        /**
         * Called when the event creation fails.
         * @param e Exception containing details of the failure.
         */
        void onFailure(Exception e);
    }

    /**
     * Creates a new event in the Firestore database.
     * @param event The event object containing details to be stored.
     * @param callback Callback to handle success or failure of the event creation.
     */
    // TODO: Include all other fields an event should have
    public void createEvent(Event event, EventCreationCallback callback) {
        // Create a map of event details
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("name", event.getName());
        eventData.put("description", event.getDescription());
        eventData.put("poster", event.getPoster());
        eventData.put("lotteryEndDate", event.getLotteryEndDate());

        // Add the event to Firestore under the "events" collection
        database.collection("events")
                .add(eventData)
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Callback interface for handling the result of a user creation operation.
     */
    public interface UserCreationCallback {
        /**
         * Called when the user is successfully created.
         * @param deviceId The device ID of the created user.
         */
        void onSuccess(String deviceId);

        /**
         * Called when the user creation fails.
         * @param e Exception containing details of the failure.
         */
        void onFailure(Exception e);
    }

    /**
     * Adds a new user to the Firestore database.
     * @param user The User object containing user details.
     * @param callback Callback to handle success or failure of the user creation.
     */
    public void addUser(User user, UserCreationCallback callback) {
        String deviceId = user.getDeviceId();
        // Create a map of user details
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("phoneNumber", user.getPhoneNumber());
        userData.put("role", "User");
        userData.put("profilePicture", "default_profile_pic");

        // Add the user to Firestore database under the "users" collection
        database.collection("users")
                .document(deviceId)  // Use the deviceId as the document Id
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(deviceId);
                })
                .addOnFailureListener(callback::onFailure);
    }


    // TODO: Implement the following interface and function

    public interface UserRetrievalCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public void getUser(String deviceId, UserRetrievalCallback callback) {
        database.collection("users")
                .document(deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            String email = document.getString("email");
                            String phoneNumber = document.getString("phoneNumber");
                            String profilePicture = document.getString("profilePicture");
                            String role = document.getString("role");
                            User user;
                            switch (role) {
//                                case "Administrator":
//                                    // user = new Administrator(deviceId, name, email, profilePicture);
//                                    // TODO: put administrator data in database
//                                    break;
                                case "Organizer":
                                    // Retrieve joinedEvents, pendingEvents, and createdEvents for Organizer
                                    ArrayList<String> joinedEvents = (ArrayList<String>) document.get("joinedEvents");
                                    ArrayList<String> pendingEvents = (ArrayList<String>) document.get("pendingEvents");
                                    ArrayList<String> createdEvents = (ArrayList<String>) document.get("createdEvents");
                                    user = new Organizer(deviceId, name, email, phoneNumber, profilePicture, joinedEvents, pendingEvents, createdEvents);
                                    break;
                                case "Entrant":
                                default:
                                    // Retrieve joinedEvents and pendingEvents for Entrant
                                    ArrayList<String> joinedEventsEntrant = (ArrayList<String>) document.get("joinedEvents");
                                    ArrayList<String> pendingEventsEntrant = (ArrayList<String>) document.get("pendingEvents");
                                    user = new Entrant(deviceId, name, email, phoneNumber, profilePicture, joinedEventsEntrant, pendingEventsEntrant);
                                    break;
                            }
                            callback.onSuccess(user);
                        } else {
                            callback.onFailure(new Exception("User does not exist."));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }
}
