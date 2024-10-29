package com.example.sphinxevents;

public class User {
    private final String deviceId;
    private String name;  // Default empty
    private String email;  // Default empty
    private String phoneNumber; // Default empty
    private String profilePicture;  // Default pfp


    // Constructor
    public User(String deviceId, String name, String email, String phoneNumber, String profilePicture) {
        this.deviceId = deviceId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePicture = profilePicture;
    }

    public void setName(String name) { this.name = name;}

    public void setEmail(String email) { this.email = email;}

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber;}

    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture;}

    public String getName() { return this.name;}

    public String getEmail() { return this.email;}

    public String getPhoneNumber() { return this.phoneNumber;}

    public String getProfilePicture() { return this.profilePicture;}

    public String getDeviceId() { return this.deviceId;}
}
