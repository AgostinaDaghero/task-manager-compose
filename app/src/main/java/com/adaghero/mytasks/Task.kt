package com.adaghero.mytasks

import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class Task(
    val id: Int,
    val title: String,
    var isDone: Boolean = false
)
