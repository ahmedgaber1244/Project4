package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var database: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        remindersLocalRepository =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        // GIVEN - Insert a task.
        val reminder = ReminderDTO("title", "description", "location", 37.422160, -122.084270)

        remindersLocalRepository.saveReminder(reminder)

        // WHEN - Get the task by id from the repo.
        val result = remindersLocalRepository.getReminder(reminder.id) as Result.Success

        // THEN - The loaded data contains the expected values.
        assertThat(result.data, CoreMatchers.notNullValue())
        assertThat(result.data.id, CoreMatchers.`is`(reminder.id))
        assertThat(result.data.title, CoreMatchers.`is`(reminder.title))
        assertThat(result.data.description, CoreMatchers.`is`(reminder.description))
        assertThat(result.data.latitude, CoreMatchers.`is`(reminder.latitude))
        assertThat(result.data.longitude, CoreMatchers.`is`(reminder.longitude))
    }

    @Test
    fun deleteAllRemindersAndGetAllReminders() = runBlockingTest {
        // 1. Insert a reminder into the repo.
        val reminder = ReminderDTO("title", "description", "location", 37.422160, -122.084270)

        remindersLocalRepository.saveReminder(reminder)

        //2.delete all reminders
        remindersLocalRepository.deleteAllReminders()

        //3.then check for zero elements
        val reminderDTOList = remindersLocalRepository.getReminders() as Result.Success
        TestCase.assertNotNull(reminderDTOList)
        assertThat(reminderDTOList.data.size, CoreMatchers.`is`(0))
    }

    @Test
    fun getReminderWithNotSavedReminderId() = runBlockingTest {
        // 1. get reminder from the repo by id.
        val reminder = ReminderDTO("title", "description", "location", 37.422160, -122.084270)

        val reminderById = remindersLocalRepository.getReminder(reminder.id) as Result.Error

        //3.then check element is null and message not found
        TestCase.assertNull(reminderById)
        assertThat(reminderById.message,CoreMatchers.`is`("Reminder not found!") )
    }

}