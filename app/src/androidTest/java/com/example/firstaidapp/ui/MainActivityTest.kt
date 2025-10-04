package com.example.firstaidapp.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.firstaidapp.MainActivity
import com.example.firstaidapp.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testBottomNavigationWorks() {
        // Test Home tab is selected by default
        onView(withId(R.id.navigation_home))
            .check(matches(isDisplayed()))

        // Test navigation to Search tab
        onView(withId(R.id.navigation_search))
            .perform(click())
        onView(withId(R.id.searchView))
            .check(matches(isDisplayed()))

        // Test navigation to Contacts tab
        onView(withId(R.id.navigation_contacts))
            .perform(click())
        onView(withId(R.id.recyclerViewContacts))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testSearchFunctionality() {
        // Navigate to search tab
        onView(withId(R.id.navigation_search))
            .perform(click())

        // Type in search query
        onView(withId(R.id.searchView))
            .perform(click())
            .perform(typeText("CPR"))

        // Verify search results are displayed
        onView(withId(R.id.recyclerViewSearchResults))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testHomeScreenDisplaysCategories() {
        // Verify home screen displays guide categories
        onView(withId(R.id.recyclerViewCategories))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testEmergencyContactsDisplayed() {
        // Navigate to contacts tab
        onView(withId(R.id.navigation_contacts))
            .perform(click())

        // Verify emergency contacts are displayed
        onView(withId(R.id.recyclerViewContacts))
            .check(matches(isDisplayed()))
    }
}
