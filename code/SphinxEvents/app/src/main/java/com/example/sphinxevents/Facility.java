
package com.example.sphinxevents;

import java.io.Serializable;

public class Facility implements Serializable {

    private String name;
    private String location;
    private String phoneNumber;
    private String ownerId;

    // Empty Constructor
    public Facility() {
    }

    Facility(String name, String location, String phoneNumber, String ownerId) {
        this.name = name;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOwnerId() {
        return ownerId;
    }
}
