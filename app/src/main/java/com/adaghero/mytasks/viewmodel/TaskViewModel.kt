package com.adaghero.mytasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.adaghero.mytasks.data.TaskRepository
import com.adaghero.mytasks.model.Priority
import com.adaghero.mytasks.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.*
import java.util.UUID
import androidx.lifecycle.viewModelScope
import android.util.Log

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _filter = MutableStateFlow<Priority?>(null) // null = All
    val filter: StateFlow<Priority?> = _filter

    /** List grouped by priority */
    val groupedTasks: StateFlow<Map<Priority, List<Task>>> =
        combine(_tasks, _filter) { tasks, filter ->
            val sorted = sortTasks(tasks)
            val filtered = filter?.let { pr -> sorted.filter { it.priority == pr } } ?: sorted
            filtered.groupBy { it.priority }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    init {
        loadTasks()
    }

    private fun sortTasks(tasks: List<Task>) =
        tasks.sortedBy { it.priority.rank }

    private fun loadTasks() {
        val loadedTasks = TaskRepository.loadTasks(getApplication())
       // Sort by priority (HIGH - MEDIUM - LOW)
        _tasks.value = sortTasks(loadedTasks)
    }

    private fun saveTasks() {
        try {
            TaskRepository.saveTasks(getApplication(), _tasks.value)
        } catch (e: Exception) {
            Log.e("TaskViewModel", "Error saving tasks", e)
        }
    }

    fun addTask(title: String, priority: Priority = Priority.MEDIUM) {
        if (title.isBlank()) return
        val newTask = Task(
            id = UUID.randomUUID().toString(),
            title = title,
            isDone = false,
            priority = priority
        )
        _tasks.value = (_tasks.value + newTask).sortedBy { it.priority.rank }
        saveTasks()
    }

    fun toggleTaskDone(taskId: String, done: Boolean) {
        _tasks.value = sortTasks(
            _tasks.value.map { task ->
                if (task.id == taskId) task.copy(isDone = done) else task
            }
        )
        saveTasks()
    }

    fun deleteTask(taskId: String) {
        _tasks.value = sortTasks(
            _tasks.value.filter { it.id != taskId }
        )
        saveTasks()
    }

    fun setFilter(priority: Priority?) {
        _filter.value = priority
    }
}