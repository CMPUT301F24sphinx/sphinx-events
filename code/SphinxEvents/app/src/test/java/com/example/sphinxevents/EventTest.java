/*
 * Class Name: EventTest
 * Date: 2024-12-02
 *
 * Description:
 * Unit test class for the Event class.
 * Tests core functionalities, including event attributes, lottery management,
 * registration deadlines, and entrant handling.
 */

package com.example.sphinxevents;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

import com.example.sphinxevents.UserLocation;

/**
 * Unit test class for the Event class.
 * Tests core functionalities, including event attributes, lottery management,
 * registration deadlines, and entrant handling.
 */
public class EventTest {

    private Event event;
    private Date pastDate;

    /**
     * Sets up test data and initializes the Event object.
     */
    @Before
    public void setUp() {
        Date futureDate = new Date(System.currentTimeMillis() + 100000); // 100 seconds in the future
        pastDate = new Date(System.currentTimeMillis() - 100000);  // 100 seconds in the past

        ArrayList<String> joinedUsers = new ArrayList<>();
        joinedUsers.add("User1");
        joinedUsers.add("User2");

        UserLocation facilityLocation = new UserLocation(10.0, 20.0);

        event = new Event(
                "Organizer1",
                "Test Event",
                "This is a test event.",
                futureDate,
                2,
                true,
                joinedUsers,
                facilityLocation
        );
    }

    /**
     * Tests the setName and getName methods.
     */
    @Test
    public void testSetAndGetEventName() {
        event.setName("New Event Name");
        assertEquals("New Event Name", event.getName());
    }

    /**
     * Tests the setDescription and getDescription methods.
     */
    @Test
    public void testSetAndGetDescription() {
        event.setDescription("Updated Description");
        assertEquals("Updated Description", event.getDescription());
    }

    /**
     * Tests the canLotteryBeDrawn method for determining if the lottery can be drawn.
     */
    @Test
    public void testLotteryCanBeDrawn() {
        event.setLotteryEndDate(pastDate);
        assertTrue(event.canLotteryBeDrawn());

        event.setLotteryWasDrawn(true);
        assertFalse(event.canLotteryBeDrawn());
    }

    /**
     * Tests the hasRegistrationDeadlinePassed method for registration deadline handling.
     */
    @Test
    public void testHasRegistrationDeadlinePassed() {
        assertFalse(event.hasRegistrationDeadlinePassed());

        event.setLotteryEndDate(pastDate);
        assertTrue(event.hasRegistrationDeadlinePassed());
    }

    /**
     * Tests the checkIfWaitingListFull method to validate waiting list capacity logic.
     */
    @Test
    public void testWaitingListFull() {
        assertTrue(event.checkIfWaitingListFull());

        event.getEntrants().remove(1);
        assertFalse(event.checkIfWaitingListFull());
    }

    /**
     * Tests the setPoster and getPoster methods for managing the event's poster URL.
     */
    @Test
    public void testGetAndSetPoster() {
        event.setPoster("http://example.com/poster.jpg");
        assertEquals("http://example.com/poster.jpg", event.getPoster());
    }

    /**
     * Tests entrant management methods, including adding and retrieving entrants.
     */
    @Test
    public void testEntrantManagement() {
        event.getEntrants().add("User3");
        assertEquals(3, event.retrieveNumInWaitingList());

        ArrayList<String> entrants = new ArrayList<>();
        entrants.add("User4");
        event.setEntrants(entrants);

        assertEquals(1, event.retrieveNumInWaitingList());
        assertEquals("User4", event.getEntrants().get(0));
    }

    /**
     * Tests the setConfirmed and getConfirmed methods for managing confirmed users.
     */
    @Test
    public void testSetAndGetConfirmed() {
        ArrayList<String> confirmedUsers = new ArrayList<>();
        confirmedUsers.add("User1");

        event.setConfirmed(confirmedUsers);
        assertEquals(1, event.getConfirmed().size());
        assertEquals("User1", event.getConfirmed().get(0));
    }
}
