/*
 * Class Name: Facility
 * Date: 2024-10-20
 *
 * Description:
 * Represents organizer's facility
 * Stores facility name, location, and phone number
 *
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

    /**
     * Emtpy constructor
     */
    public Facility() {
    }

    /**
     * Constructor for facility
     * @param name name of facility
     * @param location location of facility
     * @param phoneNumber phone number of facility
     * @param ownerId id of owner of facility
     */
    Facility(String name, UserLocation location, String phoneNumber, String ownerId) {
        this.name = name;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.ownerId = ownerId;
    }

    /**
     * Gets the name.
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the location.
     * @return the user's location
     */
    public UserLocation getLocation() {
        return location;
    }

    /**
     * Gets the phone number.
     * @return the user's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the name.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the location.
     * @param location the location to set
     */
    public void setLocation(UserLocation location) {
        this.location = location;
    }

    /**
     * Sets the phone number.
     * @param phoneNumber the phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the owner ID.
     * @return the owner ID
     */
    public String getOwnerId() {
        return ownerId;
    }
}
