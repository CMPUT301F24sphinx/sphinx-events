
package com.example.sphinxevents;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.Value;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseManager {
    private static DatabaseManager instance;
    private FirebaseFirestore database;

    /**
     * Initializes a new DatabaseManager instance with a Firestore database connection.
     */
    public DatabaseManager() {
        database = FirebaseFirestore.getInstance();
    }

    /**
     * Returns the singleton instance of DatabaseManager
     * @return the DatabaseManager instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
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
        // Add the event to Firestore under the "events" collection
        database.collection("events")
                .add(event)
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
     * @param user The Entrant object containing user details.
     * @param callback Callback to handle success or failure of the user creation.
     */
    public void saveUser(Entrant user, UserCreationCallback callback) {
        String deviceId = user.getDeviceId();

        // Add the user to Firestore database under the "users" collection
        database.collection("users")
                .document(deviceId)  // Use the deviceId as the document Id
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(deviceId);
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Callback interface for user retrieval operations.
     */
    public interface UserRetrievalCallback {
        /**
         * Called when user is successfully retrieved.
         * @param user The retrieved user object.
         */
        void onSuccess(Entrant user);

        /**
         * Called when error occurs during user retrieval.
         * @param e The exception that occurred
         */
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

                            Entrant user = document.toObject(Entrant.class);

                            if (user != null) {

                                String role = document.getString("role");
                                // TODO: Admin role as well if needed
                                switch (role) {
                                    case "Organizer":
                                        // If needed, you can cast to Organizer or handle it here
                                        user = document.toObject(Organizer.class);
                                        break;
                                    case "Entrant":
                                    default:
                                        // No action needed, user is already of type Entrant
                                        break;
                                }
                                callback.onSuccess(user);
                            }
                        } else {
                            callback.onFailure(new Exception("User does not exist."));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }


    /**
     * Callback interface for adding facility
     */
    public interface FacilityCreationCallback {
        /**
         * Called when facility is successfully added
         */
        void onSuccess();

        /**
         * Called when error occurs when adding facility
         * @param e the exception that occurred
         */
        void onFailure(Exception e);
    }

    /**
     * Adds a facility to the database
     * @param deviceId device ID of organizer, key for facility in database
     * @param facility facility to add to database
     * @param callback Callback to handle success or failure of adding facility
     */
    public void addFacility(String deviceId, Facility facility, FacilityCreationCallback callback) {
        // Add the event to Firestore under the "facilities" collection
        database.collection("facilities")
                .document(deviceId)
                .set(facility)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();  // Notify success
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Callback interface for facility retrieval
     */
    public interface facilityRetrievalCallback {
        /**
         * Called when facility is retrieved successfully
         * @param facility the facility that was retrieved
         */
        void onSuccess(Facility facility);

        /**
         * Called when error occurs during facility retrieval
         * @param e the exception that occurred
         */
        void onFailure(Exception e);
    }

    /**
     * Retrieves a facility from the database
     * @param deviceId device ID of organizer, key of facility in database
     * @param callback Callback to handle success or failure of facility retrieval
     */
    public void getFacility(String deviceId, facilityRetrievalCallback callback) {
        database.collection("facilities")
                .document(deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            String location = document.getString("location");
                            String phoneNumber = document.getString("phoneNumber");
                            Facility facility = new Facility(name, location, phoneNumber, deviceId);
                            callback.onSuccess(facility);
                        } else {
                            callback.onFailure(new Exception("Facility does not exist."));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    /**
     * Callback interface for facility deletion
     */
    public interface FacilityRemovalCallback {
        /**
         * Called when facility is successfully removed
         * @param updatedUser the updated user object
         */
        void onSuccess(Entrant updatedUser);

        /**
         * Called when error occurs during facility deletion
         * @param e the exception that occurred
         */
        void onFailure(Exception e);
    }

    /**
     * Removes a facility from database, updates user who owns faciltiy
     * @param deviceId key for facility
     * @param callback Callback to handle success or failure of facility deletion
     */
    public void removeFacility(String deviceId, FacilityRemovalCallback callback) {
        // Remove the facility document from the Firestore collection
        // TODO: Remove events that facility is hosting
        database.collection("facilities")
                .document(deviceId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove facility from user
                    getUser(deviceId, new UserRetrievalCallback() {
                        @Override
                        public void onSuccess(Entrant user) {
                            // Updates user to be an Entrant
                            Entrant updatedUser = new Entrant(user.getDeviceId(), user.getName(), user.getEmail(),
                                    user.getPhoneNumber(), user.getDefaultPfpPath(), user.getCustomPfpUrl(),
                                    user.getJoinedEvents(), user.getPendingEvents());
                            saveUser(updatedUser, new UserCreationCallback() {
                                @Override
                                public void onSuccess(String deviceId) {
                                    callback.onSuccess(updatedUser);  // Notify success
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    callback.onFailure(new Exception("Failed to remove facility from user"));
                                }
                            });
                        }

                        @Override
                        public void onFailure(Exception e) {
                            callback.onFailure(new Exception("Failed to remove facility from user"));
                        }
                    });

                })
                .addOnFailureListener(callback::onFailure);  // Notify failure
    }

    /**
     * Callback interface for facility search
     */
    public interface FacilitySearchCallback {
        /**
         * Called when facility search is successful
         * @param facilities array of facilities that match query
         */
        void onSuccess(ArrayList<Facility> facilities);

        /**
         * Called when error occurs during facility search
         * @param e the exception that occurred
         */
        void onFailure(Exception e);
    }

    /**
     * Searches database for facilities that match name
     * @param query the facility name to find in database
     * @param callback Callback to handle success or failure of searching database
     */
    public void searchFacility(String query, FacilitySearchCallback callback) {
        // Query the facilities collection for documents with a matching name
        database.collection("facilities")
                .whereEqualTo("name", query)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Facility> facilities = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String location = document.getString("location");
                            String phoneNumber = document.getString("phoneNumber");
                            String ownerId = document.getId();
                            Facility facility = new Facility(name, location, phoneNumber, ownerId);
                            facilities.add(facility);
                        }
                        callback.onSuccess(facilities);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    /**
     * Callback interface for admin check
     */
    public interface IsAdminCallback {
        /**
         * Called when result is obtained
         * @param isAdmin boolean on whether user is admin
         */
        void onResult(boolean isAdmin);
    }

    /**
     * Checks if a user deviceId is in the administrators collection
     * @param userId The deviceId of the user to check.
     * @param callback A callback to handle the result (true if admin, false otherwise).
     */
    public void isUserAdministrator(String userId, IsAdminCallback callback) {
        database.collection("administrators")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    callback.onResult(documentSnapshot.exists());
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    callback.onResult(false);
                });
    }

    //---------------------------------------------------------------------------------------------

    public interface UploadProfilePictureCallback {
        void onSuccess(String url);
        void onFailure();
    }

    public void uploadProfilePicture(String deviceId, Uri pfpUri, UploadProfilePictureCallback callback) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profilePicRef = storageRef.child("profile_pictures/" + deviceId + ".png");

        // Start uploading the image
        profilePicRef.putFile(pfpUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // On successful upload, get the download URL
                    profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Call onSuccess callback with the download URL
                        callback.onSuccess(uri.toString());
                    }).addOnFailureListener(e -> {
                        // Notify the callback of failure to get the download URL
                        callback.onFailure();
                    });
                })
                .addOnFailureListener(e -> {
                    // Notify the callback of failure to upload the image
                    callback.onFailure();
                });
    }

    public interface DeleteProfilePictureCallback {
        void onSuccess();
        void onFailure();
    }

    public void deleteProfilePicture(String deviceId, DeleteProfilePictureCallback callback) {
        // Create a reference to the profile picture in Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profilePicRef = storageRef.child("profile_pictures/" + deviceId + ".png");

        // Delete the profile picture
        profilePicRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Call the callback on success
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    // Call the callback on failure
                    callback.onFailure();
                });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Callback interface for event retrieval
     */
    public interface eventRetrievalCallback {
        /**
         * Called when event is retrieved successfully
         * @param event the evnt that was retrieved
         */
        void onSuccess(Event event);

        /**
         * Called when error occurs during event retrieval
         * @param e the exception that occurred
         */
        void onFailure(Exception e);
    }

    /**
     * Retrieves a event from the database
     * @param eventID ID of event, key of events in database
     * @param callback Callback to handle success or failure of event retrieval
     */
    public void getEvent(String eventID, eventRetrievalCallback callback) {
        database.collection("events")
                .document(eventID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            String description = document.getString("description");
                            String poster = document.getString("poster");
                            Timestamp dateTimestamp = document.getTimestamp("lotteryEndDate");
                            Date lotteryEndDate = dateTimestamp.toDate();

                            Integer entrantLimit;
                            if(document.get("entrantLimit") != null) {
                                entrantLimit = Integer.valueOf(document.get("entrantLimit").toString());
                            } else{
                                entrantLimit = null;
                            }
                            Boolean geolocationReq = document.getBoolean("geolocationReq");
                            ArrayList<String> entrants = (ArrayList<String>) document.get("entrants");
                            Event event = new Event(name, description, poster, lotteryEndDate, entrantLimit, geolocationReq, entrants);
                            callback.onSuccess(event);
                        } else {
                            callback.onFailure(new Exception("Event does not exist."));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    /**
     * Adds Id of entree to entrants field of event
     * @param userID ID of user being added
     * @param eventID ID of event being updated
     */
    public void joinEvent(String userID, String eventID) {
        database.collection("events")
                .document(eventID)
                .update("entrants", FieldValue.arrayUnion(userID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    /**
     * Adds Id of new created event to createdEvents field of user with userID
     * @param userID The user who created the event
     * @param eventID The event that is been uploaded
     */
    public void updateOrganizerCreatedEvents(String userID, String eventID) {
        database.collection("users")
                .document(userID)
                .update("createdEvents", FieldValue.arrayUnion(eventID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    /**
     * Callback interface for created Events retrieval
     */
    public interface getCreatedEventsCallback {
        /**
         * Called when events are retrieved successfully
         * @param createdEventsID List of Id's of events
         */
        void onSuccess(List<String> createdEventsID);

        /**
         * Called when error occurs during events retrival
         * @param e the exception that occurred
         */
        void onFailure(Exception e);
    }

    /**
     * Retrieves an Event from db
     * @param userID ID of user who created event
     */
    public void getCreatedEvents(String userID, getCreatedEventsCallback callback) {
        database.collection("users")
                .document(userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<String> createdEvents = (List<String>) document.get("createdEvents");
                            callback.onSuccess(createdEvents);
                        } else {
                            callback.onFailure(new Exception("Created Events does not exist."));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    // --------------------------------------------------------------------------------------------------

    /**
     * Callback interface for createNotification
     */
    public interface NotificationCreationCallback {
        /**
         * Called when notification is created successfully
         * @param notifRef DocumentReference object to new created notification
         */
        void onSuccess(DocumentReference notifRef);

        /**
         * Called when notification creation fails
         * @param e Exception returned on failed notification retrieval
         */
        void onFailure(Exception e);
    }

    /**
     * Adds notification object to "notification" collection in database
     * @param notification The notification object being uploaded
     * @param callback Call back function on weather upload succeeded or faulted
     */
    public void createNotification(Notification notification, NotificationCreationCallback callback) {
        database.collection("notifications")
                .add(notification)
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(callback::onFailure);
    }


    /**
     * Callback interface for getNotification
     */
    public interface getNotificationsCallback {
        /**
         * Callback for successful notification retrieval
         * @param notificationIDs List of DocumentSnapShots of returned notifications
         */
        void onSuccess(List<DocumentSnapshot> notificationIDs);

        /**
         * Callback for failed notification retrieval
         * @param e Exception for failed notification retrieval
         */
        void onFailure(Exception e);
    }

    /**
     * Gets list of DocumentSnapshots of notifications sent to userID
     * @param userID The user who is the reviewer of notifications
     * @param callback Success or Fail callback
     */
    public void getNotifications(String userID, getNotificationsCallback callback) {
        database.collection("notifications")
                .whereEqualTo("toUser", userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot query = task.getResult();
                        if (!query.isEmpty()) {
                            List<DocumentSnapshot> notifDocSnapshots =  query.getDocuments();
                            callback.onSuccess(notifDocSnapshots);
                        } else {
                            callback.onFailure(task.getException());
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }
}
