package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private var reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {

    private var shouldReturnError = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        try {
            if (shouldReturnError) {
                throw Exception("Test exception")
            }
            reminders?.let { return Result.Success(ArrayList(it)) }
            return Result.Error("Reminders not found!")
        } catch (ex: Exception) {
            return Result.Error(ex.localizedMessage)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        try {
            if (shouldReturnError) {
                throw Exception("Test exception")
            }
            val findedReminder = reminders?.find { reminderDTO -> reminderDTO.id == id }
            if (findedReminder != null) {
                return Result.Success(findedReminder)
            }
            return Result.Error("Reminder not found!")
        } catch (ex: Exception) {
            return Result.Error(ex.localizedMessage)
        }
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

}