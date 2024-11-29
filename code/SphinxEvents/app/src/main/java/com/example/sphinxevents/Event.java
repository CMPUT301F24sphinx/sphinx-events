
package com.example.sphinxevents;

import com.google.firebase.firestore.auth.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Represents an event object
 */
public class Event implements Serializable {

    private String name;  // The name of the event
    private String description;  // The description of the event
    private String poster;  // The url of the poster location
    private Date lotteryEndDate;  // The end date of the lottery
    private Integer entrantLimit;  // The entrant limit for the event
    private Boolean geolocationReq;  // Boolean indicating if geolocation is required
    private ArrayList<String> entrants;  // The list of entrants who have joined the event
    private UserLocation facilityLocation;  // The location of the facility event belongs to

    //---------------------------------------------------------------------------------------
    // TODO: Change these variables to match what Aniket has implemented

    private ArrayList<String> waitingList;
    private boolean lotteryWasDrawn = false;  // Boolean indicating if lottery has been drawn
    //---------------------------------------------------------------------------------------

    // Empty Constructor
    public Event() {
    }

    /**
     * Constructs an Event with the specified attributes.
     *
     * @param name The name of the event.
     * @param description The description of the event.
     * @param poster The URL of the event's poster.
     * @param lotteryEndDate The end date of the lottery.
     * @param entrantLimit The maximum number of entrants.
     * @param geolocationReq True if geolocation is required, false otherwise.
     * @param joinedUsers The list of users who have joined the event.
     * @param facilityLocation The location of the event's facility.
     */
    Event(String name, String description, String poster, Date lotteryEndDate, Integer entrantLimit,
          Boolean geolocationReq, ArrayList<String> joinedUsers, UserLocation facilityLocation) {
        this.name = name;
        this.description = description;
        this.poster = poster;
        this.lotteryEndDate = lotteryEndDate;
        this.entrantLimit = entrantLimit;
        this.geolocationReq = geolocationReq;
        this.entrants = joinedUsers;
        this.facilityLocation = facilityLocation;
        this.waitingList = new ArrayList<>();
    }

    /**
     * Gets the name of the event.
     * @return The event name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the event.
     * @return The event description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the URL of the event poster.
     * @return The poster URL.
     */
    public String getPoster() {return poster;}

    /**
     * Gets the lottery end date for the event.
     * @return The lottery end date.
     */
    public Date getLotteryEndDate() {
        return lotteryEndDate;
    }

    /**
     * Sets the name of the event.
     * @param name The event name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the description of the event.
     * @param description The event description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the lottery end date for the event.
     * @param lotteryEndDate The lottery end date.
     */
    public void setLotteryEndDate(Date lotteryEndDate) {
        this.lotteryEndDate = lotteryEndDate;
    }

    /**
     * Sets the URL of the event poster.
     * @param poster The poster URL.
     */
    public void setPoster(String poster) {this.poster = poster;}

    /**
     * Gets the entrant limit for the event.
     * @return The entrant limit.
     */
    public Integer getEntrantLimit() {return entrantLimit;}

    /**
     * Sets the entrant limit for the event.
     * @param entrantLimit The maximum number of entrants.
     */
    public void setEntrantLimit(Integer entrantLimit) {this.entrantLimit = entrantLimit;}

    /**
     * Gets whether geolocation is required for the event.
     * @return True if geolocation is required, false otherwise.
     */
    public Boolean getGeolocationReq() {return geolocationReq;}

    /**
     * Sets whether geolocation is required for the event.
     * @param geolocationReq True if geolocation is required, false otherwise.
     */
    public void setGeolocationReq(Boolean geolocationReq) {this.geolocationReq = geolocationReq;}

    /**
     * Gets the list of entrants who have joined the event.
     * @return The list of entrants.
     */
    public ArrayList<String> getEventEntrants() {return entrants;}

    /**
     * Sets the list of entrants for the event.
     * @param entrants The list of entrants.
     */
    public void setEventEntrants(ArrayList<String> entrants) {this.entrants = entrants;}

    /**
     * Returns location of facility that event belongs to
     * @return location of facility
     */
    public UserLocation getFacilityLocation() {
        return this.facilityLocation;
    }

    //----------------------------------------------------------------------------------------
    // TODO: Determine which of these functions are used / useful


    public ArrayList<String> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(ArrayList<String> waitingList) {
        this.waitingList = waitingList;
    }

    /**
     * Sets whether lottery for event has been drawn
     * @param lotteryWasDrawn new boolean indicating if lottery was drawn
     */
    public void setLotteryWasDrawn(boolean lotteryWasDrawn) {
        this.lotteryWasDrawn = lotteryWasDrawn;
    }

    /**
     * Gets whether lottery for event has been drawn
     * @return boolean indicating if lottery was drawn
     */
    public boolean wasLotteryDrawn() {
        return lotteryWasDrawn;
    }

    /**
     * Returns whether the lottery end date has passed
     * Which means that the lottery is ready to be drawn
     * @return boolean indicating whether lottery end date has passed
     */
    public boolean canLotteryBeDrawn() {
        if (lotteryWasDrawn) {
            return false;  // Lottery has already occurred, cannot be drawn again
        }
        return hasRegistrationDeadlinePassed();  // Returns if registration deadline has passed
    }

    /**
     * Returns whether current date is after lottery registration deadline
     * @return whether registration deadline has passed
     */
    public boolean hasRegistrationDeadlinePassed() {
        Date currentDate = new Date();
        return currentDate.after(lotteryEndDate);
    }

    /**
     * Returns number of people in waiting list
     * @return size of waiting list
     */
    public int retrieveNumInWaitingList() {
        return waitingList != null ? waitingList.size() : 0;
    }
    /**
     * Returns whether waiting list is full
     * @return boolean representing whether waiting list is full
     */
    public boolean checkIfWaitingListFull() {
        return this.entrantLimit != null && this.waitingList.size() == this.entrantLimit;
    }

    //-------------------------------------------------------------------------------------------
}

