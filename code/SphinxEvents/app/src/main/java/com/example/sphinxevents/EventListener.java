package com.example.sphinxevents;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class EventListener {

    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;
    private String eventID;
    private EventUpdateCallback callback;

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
                            callback.onEventUpdated(updatedEvent); // Callback to update the UI
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
