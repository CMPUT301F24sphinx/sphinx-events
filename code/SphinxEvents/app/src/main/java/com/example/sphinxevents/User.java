package com.example.sphinxevents;

public class User {
    private final String deviceId;
    private String name;  // Default empty
    private String email;  // Default empty
    private String profilePicture;  // Default


    // Constructor
    public User(String name, String email, String profilePicture, String deviceId) {
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
        this.deviceId = deviceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getProfilePicture() {
        return this.profilePicture;
    }

    public String getDeviceId() {
        return this.deviceId;
    }
}
