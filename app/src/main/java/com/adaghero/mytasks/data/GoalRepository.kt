package com.adaghero.mytasks.data

import android.content.Context
import com.adaghero.mytasks.model.Goal
import com.adaghero.mytasks.model.Subtask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class GoalRepository(private val context: Context) {
    private val fileName = "goals.json"
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val _goals = MutableStateFlow<List<Goal>>(loadFromFile())
    val goals: StateFlow<List<Goal>> = _goals

    fun addGoal(goal: Goal) {
        _goals.value = _goals.value + goal
        saveToFile()
    }

    fun deleteGoal(goalId: String) {
        _goals.value = _goals.value.filterNot { it.id == goalId }
        saveToFile()
    }

    fun addSubtask(goalId: String, subtask: Subtask) {
        _goals.value = _goals.value.map {
            if (it.id == goalId) it.copy(subtasks = it.subtasks + subtask) else it
        }
        saveToFile()
    }

    fun deleteSubtask(goalId: String, subtaskId: String) {
        _goals.value = _goals.value.map { goal ->
            if (goal.id == goalId) {
                goal.copy(subtasks = goal.subtasks.filterNot { it.id == subtaskId })
            } else goal
        }
        saveToFile()
    }

    fun toggleSubtask(goalId: String, subtaskId: String) {
        _goals.value = _goals.value.map { goal ->
            if (goal.id == goalId) {
                goal.copy(subtasks = goal.subtasks.map { st ->
                    if (st.id == subtaskId) st.copy(completed = !st.completed) else st
                })
            } else goal
        }
        saveToFile()
    }

    private fun saveToFile() {
        val file = File(context.filesDir, fileName)
        file.writeText(json.encodeToString(_goals.value))
    }

    private fun loadFromFile(): List<Goal> {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            try {
                json.decodeFromString<List<Goal>>(file.readText())
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
}
