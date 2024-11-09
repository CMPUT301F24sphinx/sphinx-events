/*
 * Class Name: ManageProfileActivityTest
 * Date: 2024-11-06
 *
 * Description:
 * ManageProfileActivityTest is a test class designed to verify the functionality of the
 * ManageProfileActivity. It ensures that the activity behaves as expected when users interact with
 * it, including verifying navigation and state changes within the activity.
 */

package com.example.sphinxevents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.espresso.intent.Intents;


/**
 * Test class for verifying behaviour of ManageProfileActivity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ManageProfileActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // Initialize Intents before the test starts
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Intents after the test completes
        Intents.release();
    }

    /**
     * Tests whether ManageProfileActivity is triggered by clicking the "Manage Profile" button.
     */
    @Test
    public void testActivitySwitch() {
        // Perform click on profile picture to open the drawer
        onView(withId(R.id.profile_pic_button)).perform(click());

        // Perform click on "Manage Profile" button
        onView(withId(R.id.drawer_manage_profile_btn)).perform(click());

        // Check if ManageProfileActivity was launched
        intended(hasComponent(ManageProfileActivity.class.getName()));
    }

    /**
     * Tests whether the "Cancel" button in ManageProfileActivity finishes the activity.
     */
    @Test
    public void testCancelButton() {
        // Perform click on profile picture to open the drawer
        onView(withId(R.id.profile_pic_button)).perform(click());

        // Perform click on "Manage Profile" button
        onView(withId(R.id.drawer_manage_profile_btn)).perform(click());

        // Perform click on the "Cancel" button in ManageProfileActivity
        onView(withId(R.id.manage_profile_cancel)).perform(click());

        // Check if MainActivity is now displayed
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
    }
}
