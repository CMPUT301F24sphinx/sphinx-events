/*
 * Represents organizer's facility
 * Stores facility name, location, and phone number
 */

package com.example.sphinxevents;

import android.location.Location;

import java.io.Serializable;

/**
 * Represents organizer's facility
 * Stores facility name, location, and phone number
 */
public class Facility implements Serializable {

    private String name;
    private UserLocation location;
    private String phoneNumber;
    private String ownerId;

    // Empty Constructor
    public Facility() {
    }

    Facility(String name, UserLocation location, String phoneNumber, String ownerId) {
        this.name = name;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public UserLocation getLocation() {
        return location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(UserLocation location) {
        this.location = location;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOwnerId() {
        return ownerId;
    }
}
