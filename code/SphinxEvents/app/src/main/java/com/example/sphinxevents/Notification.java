/*
 * Class Name: Notification
 * Date: 2024-11-06
 *
 * Copyright (c) 2024
 * All rights reserved.
 *
 */


package com.example.sphinxevents;

/**
 * Represents a system notification to be displayed on the user's device.
 * This class holds the necessary data to generate and display Android system notifications.
 */
public class Notification {
    private String title;          // Title of the notification
    private String message;        // Body text of the notification
    private String channelID;      // The ID of the notification channel (e.g., "event_notifications")

    /**
     * Constructs a Notification object with the specified details.
     *
     * @param title the title of the notification.
     * @param message the content (body text) of the notification.
     * @param channelID the ID of the notification channel.
     */
    public Notification(String title, String message, String channelID) {
        this.title = title;
        this.message = message;
        this.channelID = channelID;
    }

    /**
     * Gets the title of the notification.
     * @return the title of the notification.
     */
    public String getTitle() { return title; }

    /**
     * Sets the title of the notification.
     * @param title the title to set.
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Gets the message (body text) of the notification.
     * @return the message of the notification.
     */
    public String getMessage() { return message; }

    /**
     * Sets the message (body text) of the notification.
     * @param message the message to set.
     */
    public void setMessage(String message) { this.message = message; }

    /**
     * Gets the ID of the notification channel.
     * @return the notification channel ID.
     */
    public String getChannelID() { return channelID; }

    /**
     * Sets the ID of the notification channel.
     * @param channelID the channel ID to set.
     */
    public void setChannelID(String channelID) { this.channelID = channelID; }

}


