
package com.example.sphinxevents;

import com.google.firebase.firestore.auth.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Represents an event object
 */
public class Event implements Serializable {

    private String organizerId;  // The id of the organizer who created the event
    private String eventId;  // The id of the event in the database
    private String name;  // The name of the event
    private String description;  // The description of the event
    private String poster;  // The url of the poster location
    private Date lotteryEndDate;  // The end date of the lottery
    private int entrantLimit;  // The entrant limit for the event
    private Integer redrawUserCount; // The total number of users we invite to join again after someone cancels
    private Boolean geolocationReq;  // Boolean indicating if geolocation is required
    private UserLocation facilityLocation;  // The location of the facility event belongs to
    private ArrayList<String> entrants;  // The list of entrants who have joined the waiting list
    private ArrayList<String> lotteryWinners; // The list of entrants who won the initial lottery
    private ArrayList<String> confirmed; // the list of entrants who choose to confirm the event
    private ArrayList<String> cancelled; // The list of entrants who won the lottery and cancelled
    private boolean lotteryWasDrawn = false;  // Boolean indicating if lottery has been drawn

    // No-argument constructor
    public Event() {}

    /**
     * Constructs an Event with the specified attributes.
     *
     * @param name The name of the event.
     * @param description The description of the event.
     * @param lotteryEndDate The end date of the lottery.
     * @param entrantLimit The maximum number of entrants.
     * @param geolocationReq True if geolocation is required, false otherwise.
     * @param joinedUsers The list of users who have joined the event.
     * @param facilityLocation The location of the event's facility.
     */
    Event(String organizerId, String name, String description, Date lotteryEndDate, int entrantLimit,
          Boolean geolocationReq, ArrayList<String> joinedUsers, UserLocation facilityLocation) {
        this.organizerId = organizerId;
        this.eventId = null;
        this.name = name;
        this.description = description;
        this.poster = null;
        this.lotteryEndDate = lotteryEndDate;
        this.entrantLimit = entrantLimit;
        this.geolocationReq = geolocationReq;
        this.entrants = joinedUsers;
        this.facilityLocation = facilityLocation;
        this.lotteryWinners = new ArrayList<>();
        this.confirmed = new ArrayList<>();
        this.cancelled = new ArrayList<>();
        this.lotteryWasDrawn = false;
        this.redrawUserCount = 0;
    }

    /**
     * Gets id of organizer
     * @return ID of organizer
     */
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * Gets ID of Event
     * @return ID of event
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets ID of Event
     * @param eventId the new ID
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
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
     * Gets the number of total users who got another chance to join the event after losing the first draw
     * @return The total redrawn user count
     */
    public Integer getRedrawUserCount() {return redrawUserCount;}

    /**
     * Sets the number of total users who got another chance to join the event after losing the first draw
     * @param _redrawnUserCount The total redrawn users we want to set
     */
    public void setRedrawUserCount(Integer _redrawnUserCount) {this.redrawUserCount = _redrawnUserCount;}

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
    public ArrayList<String> getEntrants() {return entrants;}

    /**
     * Sets the list of entrants for the event.
     * @param entrants The list of entrants.
     */
    public void setEntrants(ArrayList<String> entrants) {this.entrants = entrants;}

    /**
     * Gets the list of entrants who won the lottery.
     * @return The list of winners.
     */
    public ArrayList<String> getLotteryWinners() {return lotteryWinners;}

    /**
     * Sets the list of winners of the lottery.
     * @param winners The list of winners.
     */
    public void setLotteryWinners(ArrayList<String> winners) {this.lotteryWinners = winners;}

//    /**
//     * Gets the list of entrants who lost the lottery.
//     * @return The list of losers.
//     */
//    public ArrayList<String> getLotteryLosers() {return lotteryLosers;}
//
//    /**
//     * Sets the list of losers of the lottery.
//     * @param losers The list of losers.
//     */
//    public void setLotteryLosers(ArrayList<String> losers) {this.lotteryLosers = losers;}

    /**
     * Gets the list of entrants who confirmed the event.
     * @return The list of losers.
     */
    public ArrayList<String> getConfirmed() {return confirmed;}

    /**
     * Sets the list of users who confirmed the event.
     * @param confirmed The list of users who confirm the event.
     */
    public void setConfirmed(ArrayList<String> confirmed) {this.confirmed = confirmed;}

    /**
     * Gets the list of entrants who cancelled the event.
     * @return The list of cancellers.
     */
    public ArrayList<String> getCancelled() {return cancelled;}

    /**
     * Sets the list of users who cancelled the event.
     * @param cancelled The list of users who cancel the event.
     */
    public void setCancelled(ArrayList<String> cancelled) {this.cancelled = cancelled;}

    /**
     * Returns location of facility that event belongs to
     * @return location of facility
     */
    public UserLocation getFacilityLocation() {
        return this.facilityLocation;
    }

    //----------------------------------------------------------------------------------------
    // TODO: Determine which of these functions are used / useful


//    public ArrayList<String> getWaitingList() {
//        return waitingList;
//    }
//
//    public void setWaitingList(ArrayList<String> waitingList) {
//        this.waitingList = waitingList;
//    }

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
    public boolean getLotteryWasDrawn() {
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

//    /**
//     * Returns number of people in waiting list
//     * @return size of waiting list
//     */
//    public int retrieveNumInWaitingList() {
//        return waitingList != null ? waitingList.size() : 0;
//    }
//    /**
//     * Returns whether waiting list is full
//     * @return boolean representing whether waiting list is full
//     */
//    public boolean checkIfWaitingListFull() {
//        return this.entrantLimit != null && this.waitingList.size() == this.entrantLimit;
//    }

    /**
     * Returns number of people in waiting list
     * @return size of waiting list
     */
    public int retrieveNumInWaitingList() {
        return entrants != null ? entrants.size() : 0;
    }
    /**
     * Returns whether waiting list is full
     * @return boolean representing whether waiting list is full
     */
    public boolean checkIfWaitingListFull() {
        return this.entrantLimit != 0 && this.entrants.size() == this.entrantLimit;
    }

    //-------------------------------------------------------------------------------------------
}

