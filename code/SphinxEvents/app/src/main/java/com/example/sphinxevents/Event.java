
package com.example.sphinxevents;

import java.util.Date;

public class Event {

    private String name;
    private String description;
    private String poster;
    private Date lotteryEndDate;

    Event(String name, String description, Date lotteryEndDate) {
        this.name = name;
        this.description = description;
        this.lotteryEndDate = lotteryEndDate;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPoster() {return poster;}

    public Date getLotteryEndDate() {
        return lotteryEndDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLotteryEndDate(Date lotteryEndDate) {
        this.lotteryEndDate = lotteryEndDate;
    }

    public void setPoster(String poster) {this.poster = poster;}
}

