package com.adaghero.mytasks.data

import android.content.Context
import com.adaghero.mytasks.model.Habit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object HabitRepository {
    private const val FILE_NAME = "habits.json"

    private val json = Json{
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun loadHabits(context: Context): List<Habit> {
        val file = File(context.filesDir, FILE_NAME)
        return if (!file.exists()) {
            emptyList()
        } else {
            try {
                json.decodeFromString(file.readText())
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    fun saveHabits(context: Context, habits: List<Habit>) {
        val file = File(context.filesDir, FILE_NAME)
        file.writeText(json.encodeToString(habits))
    }

}