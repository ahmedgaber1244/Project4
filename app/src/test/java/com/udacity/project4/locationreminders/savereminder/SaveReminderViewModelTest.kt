package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeTestRepository
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.R
import junit.framework.Assert.*

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [32])
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var remindersRepository: FakeTestRepository

    @Before
    fun setupStatisticsViewModel() {
        // Initialise the repository with no tasks.
        remindersRepository = FakeTestRepository()

        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext() as Application,
            remindersRepository
        )
    }

    @After
    fun cleanUp() =mainCoroutineRule.runBlockingTest{
        remindersRepository.deleteAllReminders()
        stopKoin()
    }

    @Test
    fun onClearViewModelNullifyVariable()= runBlockingTest {
        saveReminderViewModel.reminderTitle.value="title"
        saveReminderViewModel.reminderDescription.value="desc"
        saveReminderViewModel.reminderSelectedLocationStr.value="location desc"
        saveReminderViewModel.latitude.value=0.0
        saveReminderViewModel.longitude.value=0.0
        saveReminderViewModel.selectedPOI.value= PointOfInterest(LatLng(0.0,0.0),"placeId","placeName")

        saveReminderViewModel.onClear()

        assertEquals(null,saveReminderViewModel.reminderTitle.getOrAwaitValue())
        assertEquals(null,saveReminderViewModel.reminderDescription.getOrAwaitValue())
        assertEquals(null,saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue())
        assertEquals(null,saveReminderViewModel.latitude.getOrAwaitValue())
        assertEquals(null,saveReminderViewModel.longitude.getOrAwaitValue())
        assertEquals(null,saveReminderViewModel.selectedPOI.getOrAwaitValue())
    }

    @Test
    fun onValidateAndSaveReminder()= runBlockingTest {
        val reminderDataItem = ReminderDataItem("", "description", "location", 0.0, 0.0)
        // When validate and saving a new reminder
        saveReminderViewModel.validateAndSaveReminder(reminderDataItem)
        // Verify the reminder isn't added.
        assertNull(remindersRepository.remindersServiceData[reminderDataItem.id])

        val snackbarText: Int? =  saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        MatcherAssert.assertThat(
            snackbarText,
            `is`(R.string.err_enter_title)
        )
    }

    @Test
    fun onValidateReminder()= runBlockingTest {
        val reminderDataItem = ReminderDataItem("title", "description", "location", 0.0, 0.0)
        val validateEnteredData = saveReminderViewModel.validateEnteredData(reminderDataItem)
        assertEquals(true,validateEnteredData)
    }

    @Test
    fun onSaveReminder()= runBlockingTest {
        val reminderDataItem = ReminderDataItem("title", "description", "location", 0.0, 0.0)
        // When adding a new reminder
        saveReminderViewModel.saveReminder(reminderDataItem)

        // Verify the reminder is added.
        assertNotNull(remindersRepository.remindersServiceData[reminderDataItem.id])
    }


}