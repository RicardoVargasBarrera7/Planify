package com.example.planify.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val isCompleted: Boolean = false
)
