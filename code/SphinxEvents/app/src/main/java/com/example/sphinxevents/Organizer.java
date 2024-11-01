
package com.example.sphinxevents;

import java.util.ArrayList;

public class Organizer extends Entrant{

    private ArrayList<String> createdEvents;
    private Facility facility;

    public Organizer(String deviceId, String name, String email, String phoneNumber, String profilePicture,
                     ArrayList<String> joinedEvents, ArrayList<String> pendingEvents, Facility facility, ArrayList<String> createdEvents) {

        super(deviceId, name, email, phoneNumber, profilePicture, joinedEvents, pendingEvents);
        setRole("Organizer");
        this.createdEvents = createdEvents;
    }

    // Getters and Setters stuff

    public void setCreatedEvents(ArrayList<String> createdEvents) {
        this.createdEvents = createdEvents;
    }
    public ArrayList<String> getCreatedEvents() { return createdEvents; }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }
}
