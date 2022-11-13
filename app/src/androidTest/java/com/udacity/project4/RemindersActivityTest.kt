package com.udacity.project4

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    )


    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext, get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext, get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun emptyReminerListShowNodata() {
        // GIVEN - On the home screen
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        //check no data textview displayed
        onView(withId(R.id.noDataTextView)).check(ViewAssertions.matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun tryToAddReminderWithNoDataEntered() {
        // GIVEN - On the home screen
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        //try to save invalid reminder
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())
        //check snackbar is displaying error
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(
            ViewAssertions.matches(
                withText(R.string.err_enter_title)
            )
        )

        activityScenario.close()
    }


    @Test
    fun tryToAddReminderWithNoLocationSelected() {
        // GIVEN - On the home screen
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        //try to add invalid reminder "no location data"
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(replaceText("title"))
        onView(withId(R.id.saveReminder)).perform(click())
        //check snackbar is displaying no location error
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(
            ViewAssertions.matches(
                withText(R.string.err_select_location)
            )
        )

        activityScenario.close()
    }

    @Test
    fun tryToAddReminderWithValidData() = runBlockingTest {
        // GIVEN - On the home screen
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        //add valid reminder
        //nav to save reminder
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.selectLocation)).perform(click())
        //nav to select location
        onView(withId(R.id.map)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.map)).perform(longClick())
        onView(withId(R.id.save)).perform(click())
        //nav back to complete reminder saving
        onView(withId(R.id.selectedLocation)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.reminderTitle)).perform(replaceText("title"))
        onView(withId(R.id.reminderDescription)).perform(replaceText("description"))
        //save reminder
        onView(withId(R.id.saveReminder)).perform(click())
        //check if reminder is displayed
        onView(withId(R.id.reminderssRecyclerView)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.reminderssRecyclerView)).check(
            ViewAssertions.matches(
                hasDescendant(
                    withText("title")
                )
            )
        )
        onView(withId(R.id.reminderssRecyclerView)).check(
            ViewAssertions.matches(
                hasDescendant(
                    withText("description")
                )
            )
        )

        activityScenario.close()
    }


}
