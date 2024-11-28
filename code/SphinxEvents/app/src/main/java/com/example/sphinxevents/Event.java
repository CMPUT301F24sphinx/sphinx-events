
package com.example.sphinxevents;

import com.google.firebase.firestore.auth.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Event implements Serializable {

    private String name;
    private String description;
    private String poster;
    private Date lotteryEndDate;
    private Integer entrantLimit;
    private Boolean geolocationReq;
    private ArrayList<String> entrants;

    Event(String name, String description, String poster, Date lotteryEndDate, Integer entrantLimit, Boolean geolocationReq, ArrayList<String> joinedUsers) {
        this.name = name;
        this.description = description;
        this.poster = poster;
        this.lotteryEndDate = lotteryEndDate;
        this.entrantLimit = entrantLimit;
        this.geolocationReq = geolocationReq;
        this.entrants = joinedUsers;
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

    public Integer getEntrantLimit() {return entrantLimit;}

    public void setEntrantLimit(Integer entrantLimit) {this.entrantLimit = entrantLimit;}

    public Boolean getGeolocationReq() {return geolocationReq;}

    public void setGeolocationReq(Boolean geolocationReq) {this.geolocationReq = geolocationReq;}

    public ArrayList<String> getEventEntrants() {return entrants;}

    public void setEventEntrants(ArrayList<String> entrants) {this.entrants = entrants;}
}

