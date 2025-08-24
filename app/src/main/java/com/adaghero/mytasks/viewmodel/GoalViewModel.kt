package com.adaghero.mytasks.viewmodel


import androidx.lifecycle.viewModelScope
import com.adaghero.mytasks.data.GoalRepository
import com.adaghero.mytasks.model.Goal
import com.adaghero.mytasks.model.Subtask
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel


class GoalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GoalRepository(application.applicationContext)

    // Expose goals as StateFlow so the UI can automatically react to changes
    val goals: StateFlow<List<Goal>> = repository.goals


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


