package com.example.sphinxevents;

public class Notification {

    private String fromEvent;
    private String toUser;
    public enum notifType {
        Message,
        LotteryResult
    }
    private notifType notificationType;
    private String message;

    Notification(String from, String to, notifType notifT){

        this.fromEvent = from;
        this.toUser = to;
        this.notificationType = notifT;
    }

    public String getFromEvent(){
        return fromEvent;
    }

    public void setFromEvent(String from){
        this.fromEvent = from;
    }

    public String getToUser(){
        return toUser;
    }

    public void setToUser(String to){
        this.toUser = to;
    }

    public notifType getNotificationType(){
        return notificationType;
    }

    public void setNotificationType(notifType type){
        this.notificationType = type;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String msg){
        this.message = msg;
    }
}
