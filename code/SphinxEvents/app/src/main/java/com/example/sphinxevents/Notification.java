package com.example.sphinxevents;

/**
 * This is the class for a notification
 * Will be expanded later only Text messages implemented
 */
public class Notification {

    private String fromEvent; // The ID of the event/organizer who sends the message
    private String eventName; // The name of the event, to easily get name
    private String toUser; // The ID of user who revives the message

    // Enum to denote whether notification is a message or lottery result
    public enum notifType {
        Message,
        LotteryResult
    }
    private notifType notificationType; // Set it to enum value
    private String message; // String of message sent by organizer/event

    // Constructor

    /**
     * The constructor of a notification object
     * @param from the ID of the event that is sending the notification
     * @param eventName The name of the event
     * @param to the ID of the user receiving notification
     * @param notifT The notification type of the notificaion, chosen from enum of this class
     */
    Notification(String from, String eventName, String to, notifType notifT){
        this.fromEvent = from;
        this.eventName = eventName;
        this.toUser = to;
        this.notificationType = notifT;
    }

    /**
     * Get the ID of the sender
     * @return The eventID of the sender
     */
    public String getFromEvent(){
        return fromEvent;
    }

    /**
     * Set the ID of the event sender
     * @param from ID of the event sending the notification
     */
    public void setFromEvent(String from){
        this.fromEvent = from;
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
     * Return the userID of the notification receiver
     * @return
     */
    public String getToUser(){
        return toUser;
    }

    /**
     * Set the userId of the notification receiver
     * @param to the new userID of the receiver
     */
    public void setToUser(String to){
        this.toUser = to;
    }

    /**
     * Getter for notification type of notificaiton object
     * @return the type of notification object is, made from enum notiftype
     */
    public notifType getNotificationType(){
        return notificationType;
    }

    /**
     * Setter for notification type
     * @param type new notification type of obj, made from enum notifytype
     */
    public void setNotificationType(notifType type){
        this.notificationType = type;
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
