package com.adaghero.mytasks.model

import kotlinx.serialization.Serializable
import java.util.UUID

data class Goal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val subtasks: List<Subtask> = emptyList()
)

@Serializable
data class Subtask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val completed: Boolean = false
)