package com.example.sphinxevents;

import java.util.ArrayList;

public class Entrant {
    private String deviceId;
    private String role;
    private String name;
    private String email;
    private String phoneNumber;
    private String profilePicture;
    private ArrayList<String> joinedEvents;
    private ArrayList<String> pendingEvents;

    // Empty Constructor
    public Entrant() {
    }

    // Constructor
    public Entrant(String deviceId, String name, String email, String phoneNumber, String profilePicture,
                   ArrayList<String> joinedEvents, ArrayList<String> pendingEvents) {
        this.deviceId = deviceId;
        this.role = "Entrant";
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePicture = profilePicture;
        this.joinedEvents = joinedEvents != null ? joinedEvents : new ArrayList<>();
        this.pendingEvents = pendingEvents != null ? pendingEvents : new ArrayList<>();
    }

    // Getters and Setters for basic fields
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return this.phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getProfilePicture() { return this.profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getDeviceId() { return this.deviceId; }

    public String getRole() { return this.role; }
    public void setRole(String role) { this.role = role; }

    // Methods for joinedEvents and pendingEvents
    public ArrayList<String> getJoinedEvents() { return this.joinedEvents; }
    public void addJoinedEvent(String eventId) { joinedEvents.add(eventId); }

    public ArrayList<String> getPendingEvents() { return this.pendingEvents; }
    public void addPendingEvent(String eventId) { pendingEvents.add(eventId); }
}
