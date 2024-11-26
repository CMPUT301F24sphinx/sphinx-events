package com.example.sphinxevents;

/**
 * This is the class for a notification
 * Will be expanded later only Text messages implemented
 */
public class Notification {

    // Enum to denote whether notification is a message or lottery result
    public enum notificationType {
        Message,
        LotteryResult
    }

    private String eventID; // The ID of the event message pertains to
    private String eventName; // The name of the event, to easily get name
    private notificationType type; // The type of the notification
    private String message; // String of message sent by organizer/event

    /**
     * The constructor of a notification object
     * @param from the ID of the event that is sending the notification
     * @param eventName The name of the event
     * @param notificationType The notification type of the notification, chosen from enum of this class
     */
    Notification(String from, String eventName, notificationType notificationType) {
        this.eventID = from;
        this.eventName = eventName;
        this.type = notificationType;
    }

    /**
     * Get the ID of the sender
     * @return The eventID of the sender
     */
    public String getEventID(){
        return eventID;
    }

    /**
     * Set the ID of the event sender
     * @param from ID of the event sending the notification
     */
    public void setEventID(String from){
        this.eventID = from;
    }

    /**
     * Get event name
     * @return the name of the sender event
     */
    public String getEventName(){
        return eventName;
    }

    /**
     * Set the name of the event sending the notification
     * @param name The new name of sender event
     */
    public void setEventName(String name){
        this.eventName = name;
    }

    /**
     * Getter for notification type of notificaiton object
     * @return the type of notification object is, made from enum notiftype
     */
    public Notification.notificationType getType(){
        return type;
    }

    /**
     * Setter for notification type
     * @param type new notification type of obj, made from enum notifytype
     */
    public void setType(Notification.notificationType type){
        this.type = type;
    }

    /**
     * Getter for string message that is sent in notificaion
     * @return the string message
     */
    public String getMessage(){
        return message;
    }

    /**
     * Setter for the message being sent
     * @param msg String for new message text
     */
    public void setMessage(String msg){
        this.message = msg;
    }
}
