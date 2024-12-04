/*
 * Class Name: ViewCreatedEventTest
 * Date: 2024-12-02
 *
 * Description:
 * Tests the functionality of the ViewCreatedEvent activity.
 */

package com.example.sphinxevents;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Tests for verifying the behavior of the ViewCreatedEvent activity.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewCreatedEventTest {

    private static final String TEST_EVENT_ID = "test_event_123";
    private static final String TEST_EVENT_NAME = "Event Name";
    private static final String TEST_EVENT_DESCRIPTION = "Event Description";

    /**
     * Rule to launch the ViewCreatedEvent activity with a test intent.
     */
    @Rule
    public ActivityScenarioRule<ViewCreatedEvent> scenarioRule = new ActivityScenarioRule<>(
            new Intent(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext(), ViewCreatedEvent.class)
                    .putExtra("eventId", TEST_EVENT_ID)
    );

    /**
     * Tests that the event name and description are displayed correctly.
     */
    @Test
    public void testEventDetailsDisplay() {
        Espresso.onView(withId(R.id.event_name_text_view))
                .check(ViewAssertions.matches(withText(TEST_EVENT_NAME)));
        Espresso.onView(withId(R.id.event_description_text_view))
                .check(ViewAssertions.matches(withText(TEST_EVENT_DESCRIPTION)));
    }

    /**
     * Tests that clicking the View Entrant Data button launches the correct intent.
     */
    @Test
    public void testViewEntrantDataButton() {
        Espresso.onView(withId(R.id.view_entrant_data_button)).perform(ViewActions.click());
        intended(hasComponent(ViewEventEntrantData.class.getName()));
        intended(hasExtra("eventId", TEST_EVENT_ID));
    }

    /**
     * Tests that clicking the Send Notification button opens the notification fragment.
     */
    @Test
    public void testSendNotification() {
        Espresso.onView(withId(R.id.send_notification_button)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("Send Notification"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    /**
     * Tests that clicking the Draw Lottery button displays the correct alert dialog.
     */
    @Test
    public void testDrawLotteryButton() {
        Espresso.onView(withId(R.id.draw_lottery_button)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("Draw Event Lottery"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
