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
    private String defaultPfpPath;  // Path to the default profile picture in local stroage
    private String customPfpUrl;  // Url of the custom profile picture in Firebase Storage
    private boolean orgNotificationsEnabled; // Notifications from organizers preference
    private boolean adminNotificationsEnabled; // Notifications from administrators preference
    private ArrayList<String> joinedEvents;  // List of event IDs the user has accepted invitation to
    private ArrayList<String> pendingEvents;  // List of event IDs the user has joined lottery for
    private ArrayList<String> notifications; // List of notification IDs sent to the user;

    /**
     * Default constructor for Entrant. Initializes an empty entrant.
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
     * @param defaultPfpPath the path to the default profile picture.
     * @param customPfpUrl   the URL for the custom profile picture.
     * @param joinedEvents   a list of event IDs that the entrant has joined.
     * @param pendingEvents  a list of event IDs that are pending for the entrant.
     */
    public Entrant(String deviceId, String name, String email, String phoneNumber, String defaultPfpPath,
                   String customPfpUrl, boolean orgNotificationsEnabled, boolean adminNotificationsEnabled,
                   ArrayList<String> joinedEvents, ArrayList<String> pendingEvents, ArrayList<String> notifications) {
        this.deviceId = deviceId;
        this.role = "Entrant";
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.defaultPfpPath = defaultPfpPath;
        this.customPfpUrl = customPfpUrl;
        this.orgNotificationsEnabled = orgNotificationsEnabled;
        this.adminNotificationsEnabled = adminNotificationsEnabled;
        this.joinedEvents = joinedEvents != null ? joinedEvents : new ArrayList<>();
        this.pendingEvents = pendingEvents != null ? pendingEvents : new ArrayList<>();
        this.notifications = notifications != null ? notifications : new ArrayList<>();
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
     * Gets the default profile picture path.
     * @return the default profile picture path.
     */
    public String getDefaultPfpPath() { return this.defaultPfpPath; }

    /**
     * Sets the default profile picture path.
     * @param profilePicture the path of the default profile picture.
     */
    public void setDefaultPfpPath(String profilePicture) { this.defaultPfpPath = profilePicture; }

    /**
     * Gets the custom profile picture URL.
     * @return the custom profile picture URL.
     */
    public String getCustomPfpUrl() { return customPfpUrl; }

    /**
     * Sets the custom profile picture URL.
     * @param customPfpUrl the URL of the custom profile picture.
     */
    public void setCustomPfpUrl(String customPfpUrl) { this.customPfpUrl = customPfpUrl; }

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
     * Gets the list of user notification IDs
     * @return a list of notification IDs
     */
    public ArrayList<String> getNotifications() {
        return notifications;
    }

    /**
     * Adds a notification to users notification list
     * @param notificationID The ID of the notification to add
     */
    public void addNotification(String notificationID) {
        notifications.add(notificationID);
    }
}
