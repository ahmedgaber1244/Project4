/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.udacity.project4.locationreminders.data


import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
@ExperimentalCoroutinesApi
class DefaultReminersRepositoryTest {

    private val reminder = ReminderDTO("title", "description","location",37.422160,-122.084270)

    private val localTasks = listOf(reminder).sortedBy { it.id }

    private lateinit var reminderLocalDataSource: FakeDataSource

    // Class under test
    private lateinit var remindersRepository: DefaultRemindersRepository

    @Before
    fun createRepository() {
        reminderLocalDataSource = FakeDataSource(localTasks.toMutableList())
        // Get a reference to the class under test
        remindersRepository = DefaultRemindersRepository(reminderLocalDataSource)
    }

    @Test
    fun getRemindersRequestsAllRemindersFromRemoteDataSource() = runBlockingTest {
        // When reminders are requested from the reminders repository
        val tasks = remindersRepository.getReminders() as Result.Success

        // Then reminders are loaded from the local data source
        assertThat(tasks.data, IsEqual(localTasks))
    }

}

