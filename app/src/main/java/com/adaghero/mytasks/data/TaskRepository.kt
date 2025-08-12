package com.adaghero.mytasks.data

import android.content.Context
import com.adaghero.mytasks.model.Task
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object TaskRepository {
    private const val FILE_NAME = "tasks.json"

    // Instancia segura de Json (evita usar APIs internas)
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun loadTasks(context: Context): List<Task> {
        val file = File(context.filesDir, FILE_NAME)
        return if (!file.exists()) {
            emptyList()
        } else {
            try {
                json.decodeFromString(file.readText())
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    fun saveTasks(context: Context, tasks: List<Task>) {
        val file = File(context.filesDir, FILE_NAME)
        file.writeText(json.encodeToString(tasks))
    }
}