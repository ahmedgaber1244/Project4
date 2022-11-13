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
import com.udacity.project4.locationreminders.data.dto.Result.Error
import com.udacity.project4.locationreminders.data.dto.Result.Success
import java.util.LinkedHashMap

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
class FakeTestRepository : ReminderDataSource {

    private var shouldReturnError = false

    var remindersServiceData: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        try {
            if (shouldReturnError) {
                throw Exception("Test exception")
            }
            return Success(remindersServiceData.values.toList())
        } catch (ex: Exception) {
            return Error(ex.localizedMessage)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersServiceData[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        try {
            if (shouldReturnError) {
                throw Exception("Test exception")
            }
            remindersServiceData[id]?.let {
                return Success(it)
            }
            return Error("Reminder not found!")
        } catch (ex: Exception) {
            return Error(ex.localizedMessage)
        }
    }

    override suspend fun deleteAllReminders() {
        remindersServiceData.clear()
    }

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }
}