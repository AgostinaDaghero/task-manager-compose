package com.adaghero.mytasks.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

@Serializable
enum class HabitFrequency {
    DAILY, WEEKLY
}

@Serializable
data class Habit(
    val id: String,
    val name: String,
    val frequency: HabitFrequency,
    val history: MutableList<String> = mutableListOf<String>(), //Dates in ISO-8601 format
    val priority: String = "LOW"
) {
    fun isCompletedToday(): Boolean {
        val today = LocalDate.now().toString()
        return history.contains(today)
    }

    fun isCompletedThisWeek(): Boolean {
        val today = LocalDate.now()
        val weekOfYear = today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
        val year = today.year
        return history.any {
            val date = LocalDate.parse(it)
            val w = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
            val y = date.year
            w == weekOfYear && y == year
        }
    }
}
