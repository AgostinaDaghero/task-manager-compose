package com.adaghero.mytasks.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class Task(
    val id: Int,
    val title: String,
    var isDone: Boolean = false
)