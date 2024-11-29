
package com.example.sphinxevents;

import java.util.ArrayList;

public class NotificationsHelper {

    public static final String ORGANIZER_CHANNEL_ID = "organizer_notifications";
    public static final String ADMIN_CHANNEL_ID = "admin_notifications";

    /**
     * Sends a notification to lottery winners for an event
     *
     * @param facility The facility that created the event
     * @param eventName The name of the event lottery is for
     * @param recipients An array of user ID's that receive the notification
     * @param callback Callback to handle success or failure of notification creation
     */
    public static void sendLotteryWinNotification(String facility, String eventName, ArrayList<String> recipients,
                                              DatabaseManager.NotificationCreationCallback callback) {
        String title = "Lottery Result: You're a Winner!";
        String message = String.format("You've been invited for %s at %s! Be sure to secure your spot" +
                "in the event details. ", eventName, facility);
        Notification notification = new Notification(title, message, ORGANIZER_CHANNEL_ID);
        DatabaseManager.getInstance().createNotification(notification, recipients, callback);
    }

    /**
     * Sends a notification to lottery participants who were not initially selected.
     *
     * @param facility The facility hosting the event
     * @param eventName The name of the event
     * @param recipients List of user IDs to notify
     * @param callback Callback to handle success or failure of notification creation
     */
    public static void sendLotteryLossNotification(String facility, String eventName, ArrayList<String> recipients,
                                            DatabaseManager.NotificationCreationCallback callback) {
        String title = "Lottery Result: You're Still in the Running!";
        String message = String.format("Unfortunately, you weren't selected for %s at %s. But don't lose hope! " +
                "You may still have a chance to be invited.", eventName, facility);
        Notification notification = new Notification(title, message, ORGANIZER_CHANNEL_ID);
        DatabaseManager.getInstance().createNotification(notification, recipients, callback);
    }


    /**
     * Sends a notification to selected entrants who accepted the invitation.
     *
     * @param facility The facility hosting the event
     * @param eventName The name of the event
     * @param recipients List of user IDs to notify
     * @param callback Callback to handle success or failure of notification creation
     */
    public static void sendAcceptedEntrantsNotification(String facility, String eventName, ArrayList<String> recipients,
                                                 DatabaseManager.NotificationCreationCallback callback) {
        String title = "You're All Set!";
        String message = String.format("Thank you for confirming your spot for %s at %s. We're excited to see you there!",
                eventName, facility);
        Notification notification = new Notification(title, message, ORGANIZER_CHANNEL_ID);
        DatabaseManager.getInstance().createNotification(notification, recipients, callback);
    }

    /**
     * Sends a notification to entrants who joined the waiting list.
     *
     * @param facility The facility hosting the event
     * @param eventName The name of the event
     * @param recipients List of user IDs to notify
     * @param callback Callback to handle success or failure of notification creation
     */
    public static void sendWaitingListNotification(String facility, String eventName, ArrayList<String> recipients,
                                            DatabaseManager.NotificationCreationCallback callback) {
        String title = "You're on the Waiting List!";
        String message = String.format("You've joined the waiting list for %s at %s. Good luck!",
                eventName, facility);
        Notification notification = new Notification(title, message, ORGANIZER_CHANNEL_ID);
        DatabaseManager.getInstance().createNotification(notification, recipients, callback);
    }

    /**
     * Sends a notification to entrants who have been removed from the waiting list.
     *
     * @param facility The facility hosting the event
     * @param eventName The name of the event
     * @param recipients List of user IDs to notify
     * @param callback Callback to handle success or failure of notification creation
     */
    public static void sendCancelledNotification(String facility, String eventName, ArrayList<String> recipients,
                                          DatabaseManager.NotificationCreationCallback callback) {
        String title = "You've Been Removed from the Waiting List";
        String message = String.format("Unfortunately, you've been removed from the waiting list for %s at %s. We wish you the best of luck for future events.",
                eventName, facility);
        Notification notification = new Notification(title, message, ORGANIZER_CHANNEL_ID);
        DatabaseManager.getInstance().createNotification(notification, recipients, callback);
    }

    /**
     * Sends a custom notification with a provided title and message.
     *
     * @param title The title of the notification
     * @param message The message content of the notification
     * @param recipients List of user IDs to notify
     * @param callback Callback to handle success or failure of notification creation
     */
    public static void sendCustomNotification(String title, String message, ArrayList<String> recipients,
                                       DatabaseManager.NotificationCreationCallback callback) {

        // Create the notification with provided title, message, and channel
        Notification notification = new Notification(title, message, ORGANIZER_CHANNEL_ID);

        // Send the notification using the DatabaseManager
        DatabaseManager.getInstance().createNotification(notification, recipients, callback);
    }

}
