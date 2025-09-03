package com.adaghero.mytasks.viewmodel

import androidx.lifecycle.viewModelScope
import com.adaghero.mytasks.data.HabitRepository
import com.adaghero.mytasks.model.Habit
import com.adaghero.mytasks.model.HabitFrequency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.AndroidViewModel
import android.app.Application
import java.time.LocalDate

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> get() = _habits

    init {
        loadHabits()
    }

    fun loadHabits() {
        _habits.value = HabitRepository.loadHabits(getApplication<Application>().applicationContext)
    }

    fun addHabit(habit: Habit) {
        _habits.value = _habits.value + habit
        saveHabits()
    }

    fun deleteHabit(habitId: String) {
        _habits.value = _habits.value.filter { it.id != habitId }
        saveHabits()
    }

    fun markCompleted(habitId: String) {
        _habits.value = _habits.value.map { habit ->
            if (habit.id == habitId) {
                val updatedHistory = habit.history.toMutableList()
                val today = LocalDate.now().toString()

                when (habit.frequency) {
                    HabitFrequency.DAILY -> {
                        if (!habit.isCompletedToday()) updatedHistory.add(today)
                    }

                    HabitFrequency.WEEKLY -> {
                        if (!habit.isCompletedThisWeek()) updatedHistory.add(today)
                    }
                }
                habit.copy(history = updatedHistory)
            } else habit
        }
        saveHabits()
    }

    private fun saveHabits() {
        viewModelScope.launch {
            HabitRepository.saveHabits(
                getApplication<Application>().applicationContext,
                _habits.value
            )
        }
    }
}
