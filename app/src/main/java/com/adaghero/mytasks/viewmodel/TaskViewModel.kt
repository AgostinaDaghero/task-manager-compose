package com.adaghero.mytasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.adaghero.mytasks.data.TaskRepository
import com.adaghero.mytasks.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        loadTasks()
    }

    private fun loadTasks() {
        val loadedTasks = TaskRepository.loadTasks(getApplication())
        _tasks.value = loadedTasks
    }

    private fun saveTasks() {
        TaskRepository.saveTasks(getApplication(), _tasks.value)
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        val newTask = Task(
            id = UUID.randomUUID().toString(),
            title = title,
            isDone = false
        )
        _tasks.value = _tasks.value + newTask
        saveTasks()
    }

    fun toggleTaskDone(taskId: String, done: Boolean) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) task.copy(isDone = done) else task
        }
        saveTasks()
    }

    fun deleteTask(taskId: String) {
        _tasks.value = _tasks.value.filter { it.id != taskId }
        saveTasks()
    }
}