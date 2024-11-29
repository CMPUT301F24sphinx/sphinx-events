/*
 * Class Name: DatabaseManager
 * Date: 2024-11-06
 *
 * Description:
 * This class is responsible for managing interactions with Firebase Firestore and Firebase Storage.
 * It includes functionality for storing and retrieving data, as well as handling operations related to
 * user and event management. Some methods are currently under development and will be added as needed.
 */

package com.example.sphinxevents;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.Value;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//TODO: Add a more descriptive comment for the class when more functionality is implemented.
/**
 * This class manages interactions with a Firebase Firestore database and Firebase Storage.
 * It provides methods for handling data storage, retrieval, and other operations related to
 * user and event management.
 */
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

    /**
     * Retrieves a user from the Firestore database using the users device ID
     * @param deviceId The device ID of the user
     * @param callback Callback to handle success or failure of user retrieval
     */
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
                            Facility facility = document.toObject(Facility.class);
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
                                    user.isOrgNotificationsEnabled(), user.isAdminNotificationsEnabled(),
                                    user.getJoinedEvents(), user.getPendingEvents(), user.getNotifications());
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
        // Ensure the query is not case-sensitive and add a termination character to simulate a "contains" query
        String endQuery = query + "\uf8ff"; // \uf8ff is a high Unicode character

        // Query the facilities collection for documents with names containing the query
        database.collection("facilities")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", endQuery)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Facility> facilities = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Facility facility = document.toObject(Facility.class);
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

    /**
     * Callback interface for uploading a profile picture.
     */
    public interface UploadProfilePictureCallback {
        /**
         * Called when the profile picture is successfully uploaded.
         * @param url The download URL of the uploaded profile picture.
         */
        void onSuccess(String url);

        /**
         * Called when the profile picture upload fails.
         */
        void onFailure();
    }

    /**
     * Uploads a profile picture to Firebase Storage.
     * @param deviceId The device ID of the user for whom the profile picture is being uploaded.
     * @param pfpUri URI of the profile picture to upload
     * @param callback Callback to handle success of failure of the upload.
     */
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

    /**
     * Callback interface for deleting a profile picture.
     */
    public interface DeleteProfilePictureCallback {
        /**
         * Called when the profile picture is successfully deleted.
         */
        void onSuccess();

        /**
         * Called when profile picture deletion fails.
         */
        void onFailure();
    }

    /**
     * Deletes a profile picture from Firebase Storage.
     * @param deviceId The device ID of the user whose profile picture is being deleted.
     * @param callback Callback to handle success or failure of the deletion.
     */
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
                            Event event = document.toObject(Event.class);

                            // Ensure that the waitingList is initialized
                            if (event != null && event.getWaitingList() == null) {
                                event.setWaitingList(new ArrayList<>());  // Initialize waitingList if null
                            }

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
    public interface retrieveEventListCallback {
        /**
         * Called when events are retrieved successfully
         * @param events List of Event objects that were retrieved
         */
        void onSuccess(List<Event> events);

        /**
         * Called when error occurs during events retrieval
         * @param e the exception that occurred
         */
        void onFailure(Exception e);
    }

    /**
     * Retrieves events that match an ArrayList of event id's
     * Used for displaying events in home screen
     * @param eventsToRetrieve list of event id's to search for in database
     * @param callback callback to handle success or failure of event list retrieval
     */
    public void retrieveEventList(ArrayList<String> eventsToRetrieve, retrieveEventListCallback callback) {
        // Handles empty list of events to search for
        if (eventsToRetrieve.isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        // Searches database for events whose id's are in the eventsToRetrieve array
        database.collection("events")
                .whereIn(FieldPath.documentId(), eventsToRetrieve)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Event> events = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            events.add(event);
                        }
                        callback.onSuccess(events);
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
         */
        void onSuccess();

        /**
         * Called when notification creation fails
         * @param e Exception returned on failed notification retrieval
         */
        void onFailure(Exception e);
    }

    /**
     * Adds notification object to "notification" collection in the database.
     *
     * @param notification The notification object being uploaded.
     * @param recipients   The recipients of the notification.
     * @param callback     Callback function on whether upload succeeded or faulted.
     */
    public void createNotification(Notification notification, ArrayList<String> recipients,
                                   NotificationCreationCallback callback) {
        WriteBatch batch = database.batch();
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

        // Determine notification type based on channel ID
        String channelID = notification.getChannelID();
        boolean isOrganizerNotification = channelID.equals(NotificationsHelper.ORGANIZER_CHANNEL_ID);
        boolean isAdminNotification = channelID.equals(NotificationsHelper.ADMIN_CHANNEL_ID);

        for (String userID : recipients) {
            DocumentReference userRef = database.collection("users").document(userID);

            // Fetch user preferences
            Task<DocumentSnapshot> task = userRef.get().addOnSuccessListener(userDoc -> {
                if (userDoc.exists()) {
                    boolean orgNotificationsEnabled = userDoc.getBoolean("orgNotificationsEnabled") != null
                            && Boolean.TRUE.equals(userDoc.getBoolean("orgNotificationsEnabled"));
                    boolean adminNotificationsEnabled = userDoc.getBoolean("adminNotificationsEnabled") != null
                            && Boolean.TRUE.equals(userDoc.getBoolean("adminNotificationsEnabled"));

                    // Check if the notification should be sent based on preferences
                    if ((isOrganizerNotification && orgNotificationsEnabled)
                            || (isAdminNotification && adminNotificationsEnabled)) {
                        DocumentReference notificationRef = userRef.collection("notifications").document();
                        batch.set(notificationRef, notification);
                    }
                }
            }).addOnFailureListener(callback::onFailure);

            // Add the task to the list
            tasks.add(task);
        }

        // After all tasks are completed, commit the batch
        Tasks.whenAllSuccess(tasks).addOnSuccessListener(aVoid -> {
            batch.commit()
                    .addOnSuccessListener(unused -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);
        }).addOnFailureListener(e -> {
            callback.onFailure(e); // Handle failure
        });

        // Edge case: if no recipients, commit immediately
        if (recipients.isEmpty()) {
            batch.commit()
                    .addOnSuccessListener(unused -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);
        }
    }



    /**
     * Callback interface for getNotification
     */
    public interface getNotificationsCallback {
        /**
         * Callback for successful notification retrieval
         * @param notifications ArrayList of notifications directed to user
         */
        void onSuccess(ArrayList<Notification> notifications);

        /**
         * Callback for failed notification retrieval
         * @param e Exception for failed notification retrieval
         */
        void onFailure(Exception e);
    }

    /**
     * Gets ArrayList of Notification objects sent to userID
     * @param userID The list of the user to retrieve notifications for
     * @param callback Success or Failure callback
     */
    public void getNotifications(String userID, getNotificationsCallback callback) {
        database.collection("users")
                .document(userID)
                .collection("notifications")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ArrayList<Notification> notifications = new ArrayList<>();
                    for (DocumentSnapshot notificationDoc : querySnapshot.getDocuments()) {
                        notifications.add(notificationDoc.toObject(Notification.class));
                    }
                    callback.onSuccess(notifications);
                })
                .addOnFailureListener(callback::onFailure);
    }

    //---------------------------------------------------------------------------------------------
    //Admin profile search handling:

    /**
     * Callback interface for profiles search
     */
    public interface ProfilesSearchCallback {
        /**
         * Called when profile search is successful
         * @param users array of profiles that match query
         */
        void onSuccess(ArrayList<Entrant> users);

        /**
         * Called when error occurs during facility search
         * @param e the exception that occurred
         */
        void onFailure(Exception e);
    }

    /**
     * Searches database for users that match name
     * @param query the profile name to find in database
     * @param callback Callback to handle success or failure of searching database
     */
    public void searchProfiles(String query, ProfilesSearchCallback callback) {
        // Ensure the query is not case-sensitive and add a termination character to simulate a "contains" query
        String endQuery = query + "\uf8ff"; // \uf8ff is a high Unicode character

        // Query the users collection for documents with names containing the query
        database.collection("users")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", endQuery)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Entrant> users = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Entrant user = document.toObject(Entrant.class);
                            users.add(user);
                        }
                        callback.onSuccess(users);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    /**
     * Callback interface for user deletion
     */
    public interface ProfileRemovalCallback {
        /**
         * Called when profile is successfully removed
         */
        void onSuccess();

        /**
         * Called when error occurs during facility deletion
         * @param e the exception that occurred
         */
        void onFailure(Exception e);
    }

    /**
     * Removes a user profile from database
     * @param deviceId key for profile
     * @param callback Callback to handle success or failure of profile deletion
     */
    public void removeProfile(String deviceId, ProfileRemovalCallback callback) {
        // Remove the user from the Firestore collection
        database.collection("users")
                .document(deviceId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Call the callback on success
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);  // Notify failure

    }
}


