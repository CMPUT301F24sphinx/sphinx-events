
package com.example.sphinxevents;

import android.net.Uri;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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


    //---------------------------------------------------------------------------------------------
    public interface FacilityCreationCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

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
    //---------------------------------------------------------------------------------------------
    public interface facilityRetrievalCallback {
        void onSuccess(Facility facility);
        void onFailure(Exception e);
    }

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
                            Facility facility = new Facility(name, location, phoneNumber);
                            callback.onSuccess(facility);
                        } else {
                            callback.onFailure(new Exception("Facility does not exist."));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }
    //---------------------------------------------------------------------------------------------
    public interface FacilityRemovalCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

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
}
