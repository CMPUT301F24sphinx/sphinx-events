package com.example.sphinxevents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateEventActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testCreateEventButton() {
        // Click create event button
        onView(withId(R.id.create_event_button)).perform(click());

        // Checks if CreateEventActivity is launched
        intended(hasComponent(CreateEventActivity.class.getName()));
    }

    @Test
    public void testCreateEventCancelButton() {
        // Click create event button
        onView(withId(R.id.create_event_button)).perform(click());

        // Click Cancel button
        onView(withId(R.id.cancel_btn)).perform(click());

        // Check if user is back to MainActivity
        intended(hasComponent(MainActivity.class.getName()));
    }


}
