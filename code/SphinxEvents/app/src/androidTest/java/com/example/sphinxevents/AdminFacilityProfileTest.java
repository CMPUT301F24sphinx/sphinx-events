/*
 * Tests the UI of a admin browsing and removing facility and profile
 */

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

/**
 * Tests the UI of a admin browsing and removing facility
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminFacilityProfileTest {

    @Rule
    public ActivityScenarioRule<AdminSearchActivity> scenario = new
            ActivityScenarioRule<AdminSearchActivity>(AdminSearchActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Tests if FacilitySearchActivity launches when user clicks selects Facility filter
     */
    @Test
    public void testFacilitySearch() {
        // Click facility radio button
        onView(withId(R.id.facilities_radio_button)).perform(click());

        // Click Search button
        onView(withId(R.id.searchButton)).perform(click());

        // Checks if FacilitySearchActivity is launched
        intended(hasComponent(FacilitySearchActivity.class.getName()));
    }

    /**
     * Tests if ProfileSearchActivity launches when user clicks selects Profile filter
     */
    @Test
    public void testProfileSearch() {
        // Click facility radio button
        onView(withId(R.id.profiles_radio_button)).perform(click());

        // Click Search button
        onView(withId(R.id.searchButton)).perform(click());

        // Checks if FacilitySearchActivity is launched
        intended(hasComponent(ProfilesSearchActivity.class.getName()));
    }

}
