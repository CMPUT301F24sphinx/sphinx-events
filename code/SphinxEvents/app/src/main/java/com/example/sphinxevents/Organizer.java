/*
 * Represents a user who adds a facility to their profile
 * Adds facility and createdEvents attributes to the already created Entrant class attributes
 */

package com.example.sphinxevents;

import androidx.annotation.NonNull;

import java.util.ArrayList;


/**
 * Represents a user who adds a facility to their profile
 * Adds facility and createdEvents attributes to the already created Entrant class attributes
 */
public class Organizer extends Entrant {

    private ArrayList<String> createdEvents;
    private Facility facility;

    // Empty Constructor
    public Organizer() {
    }

    public Organizer(String deviceId, String name, String email, String phoneNumber, String profilePictureUrl,
                     boolean isCustomPfp, boolean orgNotificationsEnabled, boolean adminNotificationsEnabled,
                     ArrayList<String> joinedEvents, ArrayList<String> pendingEvents,
                     Facility facility, ArrayList<String> createdEvents) {

        super(deviceId, name, email, phoneNumber, profilePictureUrl, isCustomPfp, orgNotificationsEnabled,
                adminNotificationsEnabled, joinedEvents, pendingEvents);
        this.facility = facility;
        setRole("Organizer");
        this.createdEvents = createdEvents;
    }

    /**
     * Copy constructor that creates a new Organizer object with the same values of the given Organizer.
     *
     * @param other The organizer to copy data from.
     */
    public Organizer(Organizer other) {
        super(other);
        this.createdEvents = other.createdEvents;
        this.facility = other.facility;
    }

    /**
     * Sets the list of created events.
     *
     * @param createdEvents The list of created event IDs.
     */
    public void setCreatedEvents(ArrayList<String> createdEvents) {
        this.createdEvents = createdEvents;
    }

    /**
     * Gets the list of created events.
     *
     * @return The list of created event IDs.
     */
    public ArrayList<String> getCreatedEvents() {
        return createdEvents;
    }

    /**
     * Gets the associated facility.
     *
     * @return The facility object.
     */
    public Facility getFacility() {
        return facility;
    }

    /**
     * Sets the associated facility.
     *
     * @param facility The facility object to set.
     */
    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    /**
     * Adds an event ID to the list of created events.
     *
     * @param eventId The event ID to add.
     */
    public void addCreatedEvent(String eventId) {
        this.createdEvents.add(eventId);
    }

    /**
     * Creates a clone of the current Organizer object
     *
     * @return A new Organizer object with the same values as the current one.
     */
    @NonNull
    @Override
    public Organizer clone() {
        return new Organizer(this);
    }
}
