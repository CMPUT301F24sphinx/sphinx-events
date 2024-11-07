/*
 * Represents user when they add a facility to their profile and become an organizer
 * Adds facility and createdEvents attributes to the already created Entrant class attributes
 */

package com.example.sphinxevents;

import java.util.ArrayList;

public class Organizer extends Entrant {

    private ArrayList<String> createdEvents;
    private Facility facility;

    // Empty Constructor
    public Organizer() {
    }

    public Organizer(String deviceId, String name, String email, String phoneNumber, String defaultPfp, String customPfp,
                     ArrayList<String> joinedEvents, ArrayList<String> pendingEvents, Facility facility, ArrayList<String> createdEvents) {

        super(deviceId, name, email, phoneNumber, defaultPfp, customPfp, joinedEvents, pendingEvents);
        this.facility = facility;
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
