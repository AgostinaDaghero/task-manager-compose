package com.adaghero.mytasks.data

import android.content.Context
import com.adaghero.mytasks.model.Goal
import com.adaghero.mytasks.model.Subtask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class GoalRepository(private val context: Context) {
    private val goals = MutableStateFlow<List<Goal>>(emptyList())

    fun getGoals(): Flow<List<Goal>> = goals

    fun addGoal(goal: Goal) {
        goals.value = goals.value + goal
    }

    fun addSubtask(goalId: String, subtask: Subtask) {
        goals.value = goals.value.map {
            if (it.id == goalId) it.copy(subtasks = it.subtasks + subtask) else it
        }
    }

    fun toggleSubtask(goalId: String, subtaskId: String) {
        goals.value = goals.value.map { goal ->
            if (goal.id == goalId) {
                goal.copy(subtasks = goal.subtasks.map { st ->
                    if (st.id == subtaskId) st.copy(completed = !st.completed) else st
                })
            } else goal
        }
    }
}