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
import com.udacity.project4.utils.EspressoIdlingResource.wrapEspressoIdlingResource
import com.udacity.project4.locationreminders.data.dto.Result

/**
 * Concrete implementation to load reminders from the data sources into a cache.
 */
class DefaultRemindersRepository(private val remindersLocalDataSource: ReminderDataSource) : ReminderDataSource {

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        wrapEspressoIdlingResource {
            return remindersLocalDataSource.getReminders()
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        wrapEspressoIdlingResource {
            return remindersLocalDataSource.saveReminder(reminder)
        }
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        wrapEspressoIdlingResource {
            return remindersLocalDataSource.getReminder(id)
        }
    }

    override suspend fun deleteAllReminders() {
        wrapEspressoIdlingResource {
            return remindersLocalDataSource.deleteAllReminders()
        }
    }

}
