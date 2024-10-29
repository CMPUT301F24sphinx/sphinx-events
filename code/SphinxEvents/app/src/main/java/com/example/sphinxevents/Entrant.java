package com.example.sphinxevents;

import java.util.ArrayList;

public class Entrant extends User {
    private ArrayList<String> joinedEvents;
    private ArrayList<String> pendingEvents;

    // Constructor
    public Entrant(String deviceId, String name, String email, String profilePicture) {
        super(name, email, profilePicture, deviceId);  // Call the User constructor
        this.joinedEvents = new ArrayList<>();  // Initialize the ArrayLists
        this.pendingEvents = new ArrayList<>();
    }

    // Getters and Setters for joinedEvents and pendingEvents

    public void addJoinedEvent(String eventId) {
        joinedEvents.add(eventId);
    }

    public void addPendingEvent(String eventId) {
        pendingEvents.add(eventId);
    }

    public ArrayList<String> getJoinedEvents() {
        return this.joinedEvents;
    }

    public ArrayList<String> getPendingEvents() {
        return this.pendingEvents;
    }
}
