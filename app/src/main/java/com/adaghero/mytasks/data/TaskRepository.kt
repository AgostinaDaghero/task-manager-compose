package com.adaghero.mytasks.data

import android.content.Context
import com.adaghero.mytasks.model.Task
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object TaskRepository {
    private const val FILE_NAME = "tasks.json"

    fun loadTasks(context: Context): List<Task> {
        val file = File(context.filesDir, FILE_NAME)
        return if (!file.exists()) {
            emptyList()
        } else {
            try {
                Json.Default.decodeFromString(file.readText())
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    fun saveTasks(context: Context, tasks: List<Task>) {
        val file = File(context.filesDir, FILE_NAME)
        file.writeText(Json.Default.encodeToString(tasks))
    }
}