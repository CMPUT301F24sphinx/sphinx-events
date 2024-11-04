
package com.example.sphinxevents;

import java.util.Date;

public class Event {

    private String name;
    private String description;
    private String poster;
    private Date lotteryEndDate;
    private Integer entrantLimit;
    private Boolean geolocationReq;

    Event(String name, String description, String poster, Date lotteryEndDate, Integer entrantLimit, Boolean geolocationReq) {
        this.name = name;
        this.description = description;
        this.poster = poster;
        this.lotteryEndDate = lotteryEndDate;
        this.entrantLimit = entrantLimit;
        this.geolocationReq = geolocationReq;
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
}

