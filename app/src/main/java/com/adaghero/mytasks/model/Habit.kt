@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.adaghero.mytasks.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
enum class HabitFrequency{
    DAILY, WEEKLY
}

@Serializable
data class Habit(
    val id: String,
    val name: String,
    val frequency: HabitFrequency,
    val history: MutableList<String> = mutableListOf<String>() //Dates in ISO-8601 format
) {
    fun markCompletedToday(){
        val today = LocalDate.now().toString()
        if (!history.contains(today)){
            history.add(today)
        }
    }
}
