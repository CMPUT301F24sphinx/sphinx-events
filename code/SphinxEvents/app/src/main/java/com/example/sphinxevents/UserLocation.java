
package com.example.sphinxevents;

import java.io.Serializable;

public class UserLocation implements Serializable {

    private double latitude;
    private double longitude;

    // No-argument constructor required for Firestore deserialization
    public UserLocation() {
    }

    public UserLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
