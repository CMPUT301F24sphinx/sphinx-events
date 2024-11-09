/*
 * Tests the UI of a user managing their facility profile
 */


package com.example.sphinxevents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.sphinxevents.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the UI of a user managing their facility profile
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ManageFacilityTest {

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


    /**
     * Tests if ManageFacilityActivity launches when user clicks Manage Facility button in main screen
     */
    @Test
    public void testManageFacility() {
        // Click profile-pic to open side drawer
        onView(withId(R.id.profile_pic_button)).perform(click());

        // Click Manage Facility button
        onView(withId(R.id.drawer_manage_facility_btn)).perform(click());

        // Checks if ManageFacilityActivity is launched
        intended(hasComponent(ManageFacilityActivity.class.getName()));
    }


    /**
     * Tests ManageFacilityActivity back-button going back to MainActivity
     */
    @Test
    public void testManageFacilityBackArrow() {
        // Click profile-pic to open side drawer
        onView(withId(R.id.profile_pic_button)).perform(click());

        // Click Manage Facility button
        onView(withId(R.id.drawer_manage_facility_btn)).perform(click());

        // Click Back Arrow button
        onView(withId(R.id.manage_facility_back_btn)).perform(click());

        // Checks if drawer of main screen is displayed
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
    }


    /**
     * Tests if AddFacilityActivity opens when user clicks Add Facility button in ManageFacilityActivity
     */
    @Test
    public void testAddFacility() {
        // Click profile-pic to open side drawer
        onView(withId(R.id.profile_pic_button)).perform(click());

        // Click Manage Facility button
        onView(withId(R.id.drawer_manage_facility_btn)).perform(click());

        // Click Add Facility button
        onView(withId(R.id.add_facility_button)).perform(click());

        // Checks if AddFacilityActivity is launched
        intended(hasComponent(AddFacilityActivity.class.getName()));
    }

    /**
     * Tests if cancel button in AddFacilityActivity works
     */
    @Test
    public void testAddFacilityCancelButton() {
        // Click profile-pic to open side drawer
        onView(withId(R.id.profile_pic_button)).perform(click());

        // Click Manage Facility button
        onView(withId(R.id.drawer_manage_facility_btn)).perform(click());

        // Click Add Facility button
        onView(withId(R.id.add_facility_button)).perform(click());

        // Click Cancel button
        onView(withId(R.id.cancel_button)).perform(click());

        // Check if user is back to ManageFacilityActivity
        intended(hasComponent(ManageFacilityActivity.class.getName()));
    }
}
