
package com.example.sphinxevents;

import java.util.Date;

public class Event {

    private String name;
    private String description;
    // private Date registerDeadline;

    Event(String name, String description) {
        this.name = name;
        this.description = description;
        //this.registerDeadline = registerDeadline;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

//    public Date getRegisterDeadline() {
//        return registerDeadline;
//    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public void setRegisterDeadline(Date registerDeadline) {
//        this.registerDeadline = registerDeadline;
//    }
}
