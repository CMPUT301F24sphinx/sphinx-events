/*
 * Class Name: UserLocation
 * Date: 2024-11-30
 *
 * Description:
 * Represents a user's location with latitude and longitude coordinates
 */

package com.example.sphinxevents;

import java.io.Serializable;

/**
 * Represents a user's location with latitude and longitude coordinates.
 */
public class UserLocation implements Serializable {

    private double latitude;
    private double longitude;

    /**
     * Default constructor required for Firestore deserialization.
     */
    public UserLocation() {
    }

    /**
     * Constructs a UserLocation with specified latitude and longitude.
     *
     * @param latitude  the latitude of the location
     * @param longitude the longitude of the location
     */
    public UserLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Gets the latitude of the user's location.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the user's location.
     *
     * @param latitude the new latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude of the user's location.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the user's location.
     *
     * @param longitude the new longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

