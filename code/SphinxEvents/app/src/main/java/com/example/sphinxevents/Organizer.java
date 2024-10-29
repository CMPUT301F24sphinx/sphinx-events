
package com.example.sphinxevents;

import java.util.ArrayList;

public class Organizer extends Entrant{
    private ArrayList<String> createdEvents;

    public Organizer(String deviceId, String name, String email, String phoneNumber, String profilePicture,
                     ArrayList<String> joinedEvents, ArrayList<String> pendingEvents, ArrayList<String> createdEvents) {

        super(deviceId, name, email, phoneNumber, profilePicture, joinedEvents, pendingEvents);
        this.createdEvents = createdEvents;
    }

    // Getters and Setters stuff

}
