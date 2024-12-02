/*
 * Class Name: EventListener
 * Date: 2024-11-25
 *
 * Description:
 * Listens for changes in a given event in the database
 * Notifies listeners for changes
 *
 */

package com.example.sphinxevents;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

/**
 *  Listens for changes in a given event in the database
 *  Notifies listeners for changes
 */
public class EventListener {

    private FirebaseFirestore db;  // Reference to the database
    private ListenerRegistration listenerRegistration;  // Used to manage a real-time listener on an event
    private String eventID;  // The Id of the event to listen to changes for
    private EventUpdateCallback callback;  // The callback to notify listeners for changes

    /**
     * Constructs the EventListener
     * @param eventID the Id of the event to listen to changes for
     * @param callback the callback for which activity is listening for changes
     */
    public EventListener(String eventID, EventUpdateCallback callback) {
        this.db = FirebaseFirestore.getInstance();
        this.eventID = eventID;
        this.callback = callback;
    }

    /**
     * Interface for callback to handle update
     */
    public interface EventUpdateCallback {
        void onEventUpdated(Event event);
        void onFailure(Exception e);
    }

    /**
     * Starts listening for changes in the database document of a certain event
     */
    public void startListening() {
        listenerRegistration = db.collection("events")
                .document(eventID)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        // Handle error
                        callback.onFailure(e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Event updatedEvent = documentSnapshot.toObject(Event.class);

                        if (updatedEvent != null) {
                            callback.onEventUpdated(updatedEvent); // Callback to listeners to notify change
                        }
                    }
                });
    }

    /**
     * Stops listening for changes
     */
    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
