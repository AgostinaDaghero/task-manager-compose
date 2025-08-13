package com.adaghero.mytasks.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adaghero.mytasks.data.HabitRepository
import com.adaghero.mytasks.model.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HabitViewModel : ViewModel() {
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> get() = _habits

    fun loadHabits(context: Context) {
        _habits.value = HabitRepository.loadHabits(context)
    }

    fun addHabit(context: Context, habit: Habit) {
        val updated = _habits.value + habit
        _habits.value = updated
        saveHabits(context)
    }

    fun markCompleted(context: Context, habitId: String) {
        val updated = _habits.value.map {
            if (it.id == habitId) {
                it.apply { markCompletedToday() }
            } else it
        }
        _habits.value = updated
        saveHabits(context)
    }

    private fun saveHabits(context: Context) {
        viewModelScope.launch {
            HabitRepository.saveHabits(context, _habits.value)
        }
    }

}