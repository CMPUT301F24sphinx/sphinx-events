/*
 * Class Name: Entrant
 * Date: 2024-11-06
 *
 * Description:
 * The Entrant class represents a user who can join events. It stores the user's personal information,
 * including their name, email, phone number, notification preferences, and profile picture details.
 * It also keeps track of events the user has joined or is pending for. This class provides
 * getter and setter methods to manage the user's data.
 */

package com.example.sphinxevents;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The Entrant class represents a user participating in events.
 * It holds the user's personal details such as name, email, phone number,
 * notification preferences, and profile picture information, along with the events
 * they are involved in. An Entrant can have a default profile picture stored locally
 * or a custom profile picture in Firebase Storage. The class also keeps track of the events
 * the user has joined and the events they are still waiting for approval or entry.
 * This class provides methods for accessing and modifying the user's information
 * and event participation.
 */
public class Entrant implements Serializable {
    private String deviceId;
    private String role;  // Role of the Entrant, either "Entrant" or "Organizer"
    private String name;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl;  // Url of the profile picture in Firebase Storage
    private boolean isCustomPfp; // true if user uploaded custom pfp, false otherwise
    private boolean orgNotificationsEnabled; // Notifications from organizers preference
    private boolean adminNotificationsEnabled; // Notifications from administrators preference
    private ArrayList<String> joinedEvents;  // List of event IDs the user has accepted invitation to
    private ArrayList<String> pendingEvents;  // List of event IDs the user has joined lottery for

    /**
     * No argument constructor
     */
    public Entrant() {
    }

    /**
     * Constructs an Entrant with the specified details.
     *
     * @param deviceId       the device ID associated with the entrant.
     * @param name           the name of the entrant.
     * @param email          the email of the entrant.
     * @param phoneNumber    the phone number of the entrant.
     * @param profilePictureUrl   the URL for the profile picture.
     * @param joinedEvents   a list of event IDs that the entrant has joined.
     * @param pendingEvents  a list of event IDs that are pending for the entrant.
     */
    public Entrant(String deviceId, String name, String email, String phoneNumber,
                   String profilePictureUrl, boolean isCustomPfp, boolean orgNotificationsEnabled, boolean adminNotificationsEnabled,
                   ArrayList<String> joinedEvents, ArrayList<String> pendingEvents) {
        this.deviceId = deviceId;
        this.role = "Entrant";
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.isCustomPfp = isCustomPfp;
        this.orgNotificationsEnabled = orgNotificationsEnabled;
        this.adminNotificationsEnabled = adminNotificationsEnabled;
        this.joinedEvents = joinedEvents != null ? joinedEvents : new ArrayList<>();
        this.pendingEvents = pendingEvents != null ? pendingEvents : new ArrayList<>();
    }


    /**
     * Constructor for cloning
     * @param other entrant to clone
     */
    public Entrant(Entrant other) {
        this.deviceId = other.deviceId;
        this.role = other.role;
        this.name = other.name;
        this.email = other.email;
        this.phoneNumber = other.phoneNumber;
        this.profilePictureUrl = other.profilePictureUrl;
        this.isCustomPfp = other.isCustomPfp;
        this.orgNotificationsEnabled = other.orgNotificationsEnabled;
        this.adminNotificationsEnabled = other.adminNotificationsEnabled;
        this.joinedEvents = other.joinedEvents;
        this.pendingEvents = other.pendingEvents;
    }

    /**
     * Gets the user's name.
     * @return the user's name.
     */
    public String getName() { return this.name; }

    /**
     * Sets the user's name.
     * @param name the user's name.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the user's email.
     * @return the user's email.
     */
    public String getEmail() { return this.email; }

    /**
     * Sets the user's email.
     * @param email the user's email.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Gets the user's phone number.
     * @return the user's phone number.
     */
    public String getPhoneNumber() { return this.phoneNumber; }

    /**
     * Sets the user's phone number.
     * @param phoneNumber the user's phone number.
     */
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }


    /**
     * Gets the custom profile picture URL.
     * @return the custom profile picture URL.
     */
    public String getProfilePictureUrl() { return profilePictureUrl; }

    /**
     * Sets the custom profile picture URL.
     * @param profilePictureUrl the URL of the custom profile picture.
     */
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    /**
     * Gets the isCustomPfp attribute of the user
     * @return boolean representing whether custom pfp is selected or not
     */
    public boolean isCustomPfp() {
        return isCustomPfp;
    }

    /**
     * Sets the isCustomPfp attribute of the user
     * @param isCustomPfp The new value of isCustomPfp
     */
    public void setCustomPfp(boolean isCustomPfp) {
        this.isCustomPfp = isCustomPfp;
    }

    /**
     * Gets the user's device ID.
     * @return the user's device ID.
     */
    public String getDeviceId() { return this.deviceId; }

    /**
     * Gets the user's role.
     * @return the user's role.
     */
    public String getRole() { return this.role; }

    /**
     * Sets the user's role.
     * @param role the user's role.
     */
    public void setRole(String role) { this.role = role; }

    /**
     * Gets the user's notification preference of organizers
     * @return the user's preference for organizers notifications
     */
    public boolean isOrgNotificationsEnabled() {
        return orgNotificationsEnabled;
    }

    /**
     * Sets the user's notification preference for organizers
     * @param orgNotificationsEnabled the user's preference of organizers notifications
     */
    public void setOrgNotificationsEnabled(boolean orgNotificationsEnabled) {
        this.orgNotificationsEnabled = orgNotificationsEnabled;
    }

    /**
     * Gets the user's notification preference for administrators
     * @return the user's preference for administrator notifications
     */
    public boolean isAdminNotificationsEnabled() {
        return adminNotificationsEnabled;
    }

    /**
     * Sets the user's notification preference for administrators
     * @param adminNotificationsEnabled the user's preference of administrator notifications
     */
    public void setAdminNotificationsEnabled(boolean adminNotificationsEnabled) {
        this.adminNotificationsEnabled = adminNotificationsEnabled;
    }

    /**
     * Gets the list of joined event IDs.
     * @return a list of joined event IDs.
     */
    public ArrayList<String> getJoinedEvents() { return this.joinedEvents; }

    /**
     * Adds an event to the list of joined events.
     * @param eventId the ID of the event to add.
     */
    public void addJoinedEvent(String eventId) { joinedEvents.add(eventId); }

    /**
     * Gets the list of pending event IDs.
     * @return a list of pending event IDs.
     */
    public ArrayList<String> getPendingEvents() { return this.pendingEvents; }

    /**
     * Adds an event to the list of pending events.
     * @param eventId the ID of the event to add.
     */
    public void addPendingEvent(String eventId) { pendingEvents.add(eventId); }

    /**
     * Clones entrant
     * @return copy of entrant
     */
    @NonNull
    public Entrant clone() {
        return new Entrant(this);
    }

}
