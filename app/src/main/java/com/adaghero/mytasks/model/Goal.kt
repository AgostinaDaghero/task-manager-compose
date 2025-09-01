package com.adaghero.mytasks.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Goal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val subtasks: List<Subtask> = emptyList()
) {
    val progress: Float
        get() = if (subtasks.isEmpty()) 0f
        else subtasks.count { it.completed }.toFloat() / subtasks.size
}

@Serializable
data class Subtask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val completed: Boolean = false
)