package com.adaghero.mytasks.viewmodel

import androidx.lifecycle.viewModelScope
import com.adaghero.mytasks.data.HabitRepository
import com.adaghero.mytasks.model.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.AndroidViewModel
import android.app.Application

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

    fun markCompleted(habitId: String) {
        _habits.value = _habits.value.map {
            if (it.id == habitId) {
                it.copy().also { h -> h.markCompletedToday() }
            } else it
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