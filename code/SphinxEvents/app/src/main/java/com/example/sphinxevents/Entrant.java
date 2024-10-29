package com.example.sphinxevents;

import java.util.ArrayList;

public class Entrant extends User {
    private ArrayList<String> joinedEvents;
    private ArrayList<String> pendingEvents;

    // Constructor
    public Entrant(String deviceId, String name, String email, String phoneNumber, String profilePicture,
                   ArrayList<String> joinedEvents, ArrayList<String> pendingEvents) {
        super(name, email, phoneNumber, profilePicture, deviceId);  // Call the User constructor
        this.joinedEvents = joinedEvents;  // Initialize the ArrayLists
        this.pendingEvents = pendingEvents;
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
