package com.adaghero.mytasks.model

import kotlinx.serialization.Serializable

@Serializable
enum class Priority(val rank: Int){
    HIGH(0),
    MEDIUM(1),
    LOW(2)
}

@Serializable
data class Task(
    val id: String,
    val title: String,
    var isDone: Boolean = false,
    val priority: Priority = Priority.MEDIUM
)
