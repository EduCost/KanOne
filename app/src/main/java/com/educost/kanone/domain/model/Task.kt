package com.educost.kanone.domain.model

data class Task(
    val id: Long,
    val description: String,
    val isCompleted: Boolean,
    val position: Int
)
