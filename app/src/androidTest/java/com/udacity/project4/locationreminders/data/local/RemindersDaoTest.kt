package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull

import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantTaskExecutorRule=InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        // GIVEN - Insert a task.
        val reminder = ReminderDTO("title", "description","location",37.422160,-122.084270)

        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the task by id from the database.
        val loaded = database.reminderDao().getReminderById(reminder.id) as ReminderDTO

        // THEN - The loaded data contains the expected values.
        assertThat(loaded , notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
    }

    @Test
    fun deleteAllRemindersAndGetAllReminders() = runBlockingTest{
        // 1. Insert a reminder into the DAO.
        val reminder = ReminderDTO("title", "description","location",37.422160,-122.084270)

        database.reminderDao().saveReminder(reminder)

        //2.delete all reminders
        database.reminderDao().deleteAllReminders()

        //3.then check for zero elements
        val reminderDTOList = database.reminderDao().getReminders()
        assertNotNull(reminderDTOList)
        assertThat(reminderDTOList.size, `is`(0))
    }

    @Test
    fun getReminderWithNotSavedReminderId() = runBlockingTest{
        // 1. get reminder from the DAO by id.
        val reminder = ReminderDTO("title", "description","location",37.422160,-122.084270)

        val reminderById = database.reminderDao().getReminderById(reminder.id)

        //3.then check element is null
        assertNull(reminderById)
    }
}