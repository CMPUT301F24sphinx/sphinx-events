/*
 * Class Name: ViewEnteredEventTest
 * Date: 2024-12-02
 *
 * Description:
 * Tests the functionality of the ViewEnteredEvent activity.
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

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewEnteredEventTest {

    private static final String TEST_EVENT_ID = "event_456";
    private static final String TEST_EVENT_NAME = "Event Name";
    private static final String TEST_EVENT_DESCRIPTION = "Event Description";

    @Rule
    public ActivityScenarioRule<ViewEnteredEvent> scenarioRule = new ActivityScenarioRule<>(
            new Intent(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext(), ViewEnteredEvent.class)
                    .putExtra("eventId", TEST_EVENT_ID)
    );

    /**
     * Verifies that event details are displayed correctly.
     */
    @Test
    public void testEventDetailsDisplay() {
        Espresso.onView(withId(R.id.event_name_text_view))
                .check(ViewAssertions.matches(withText(TEST_EVENT_NAME)));
        Espresso.onView(withId(R.id.event_description_text_view))
                .check(ViewAssertions.matches(withText(TEST_EVENT_DESCRIPTION)));
    }

}
