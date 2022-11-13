package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeTestRepository
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [32])
class RemindersListViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var reminderViewModel: RemindersListViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var remindersRepository: FakeTestRepository


    @Before
    fun setupStatisticsViewModel() {
        // Initialise the repository with no tasks.
        remindersRepository = FakeTestRepository()

        reminderViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext() as Application, remindersRepository)
    }

    @After
    fun cleanUp() =mainCoroutineRule.runBlockingTest{
        remindersRepository.deleteAllReminders()
        stopKoin()
    }

    @Test
    fun loadRemindersLoadingCheck() =mainCoroutineRule.runBlockingTest{

        mainCoroutineRule.pauseDispatcher()

        // Load the reminders in the view model.
        reminderViewModel.loadReminders()

        // Then progress indicator is shown.
        MatcherAssert.assertThat(reminderViewModel.showLoading.getOrAwaitValue(), Is.`is`(true))

        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden.
        MatcherAssert.assertThat(reminderViewModel.showLoading.getOrAwaitValue(), Is.`is`(false))
    }

    @Test
    fun loadRemindersCallErrorToDisplay()=mainCoroutineRule.runBlockingTest{
        // Make the repository return errors.
        remindersRepository.setReturnError(true)
        reminderViewModel.loadReminders()

        // Then empty and error are true (which triggers an error message to be shown).
        MatcherAssert.assertThat(reminderViewModel.showNoData.getOrAwaitValue(), Is.`is`(true))
        MatcherAssert.assertThat(reminderViewModel.showSnackBar.getOrAwaitValue(), Is.`is`("Test exception"))
    }

    @Test
    fun loadRemindersCheckForEmpty() =mainCoroutineRule.runBlockingTest{
        // loadReminders with no data.
        reminderViewModel.loadReminders()

        // Then empty is true (which triggers an error message to be shown).
        MatcherAssert.assertThat(reminderViewModel.showNoData.getOrAwaitValue(), Is.`is`(true))
    }

    @Test
    fun loadRemindersCheckForNotEmpty()=mainCoroutineRule.runBlockingTest {
        val reminderDataItem = ReminderDTO("", "description", "location", 0.0, 0.0)
        // add reminder.
        remindersRepository.saveReminder(reminderDataItem)
        // loadReminders with data.
        reminderViewModel.loadReminders()

        // Then empty is true (which triggers an error message to be shown).
        MatcherAssert.assertThat(reminderViewModel.showNoData.getOrAwaitValue(), Is.`is`(false))
    }

}