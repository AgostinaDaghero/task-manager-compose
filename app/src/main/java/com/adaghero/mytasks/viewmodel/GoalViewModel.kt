package com.adaghero.mytasks.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.adaghero.mytasks.data.GoalRepository
import com.adaghero.mytasks.model.Goal
import com.adaghero.mytasks.model.Subtask
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow



class GoalViewModel(private val repository: GoalRepository) : ViewModel() {
    private val _goals = MutableStateFlow<List<Goal>>(emptyList())

    // Expose goals as StateFlow so the UI can automatically react to changes
    val goals: StateFlow<List<Goal>> = repository
        .getGoals()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Add a new goal to the repository
    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            repository.addGoal(goal)
        }
    }

    // Add a new subtask to an existing goal
    fun addSubtask(goalId: String, subtask: Subtask) {
        viewModelScope.launch {
            repository.addSubtask(goalId, subtask)
        }
    }

    // Toggle the completion state of a subtask
    fun toggleSubtask(goalId: String, subtaskId: String) {
        viewModelScope.launch {
            repository.toggleSubtask(goalId, subtaskId)
        }
    }
}

// Factory to create GoalViewModel with a GoalRepository dependency
class GoalViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoalViewModel(GoalRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

