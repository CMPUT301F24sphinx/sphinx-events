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

import android.net.Uri;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import java.util.ArrayList;

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
         * Called when facility deletion is successful
         */
        void onSuccess();

        /**
         * Called when error occurs during facility deletion
         * @param e the exception that occurred
         */
        void onFailure(Exception e);
    }

    /**
     * Removes a facility from database
     * @param deviceId key for facility
     * @param callback Callback to handle success or failure of facility deletion
     */
    public void removeFacility(String deviceId, FacilityRemovalCallback callback) {
        // Remove the facility document from the Firestore collection
        database.collection("facilities")
                .document(deviceId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();  // Notify success
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
}
