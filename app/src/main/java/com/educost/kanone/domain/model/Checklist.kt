package com.educost.kanone.domain.model

data class Checklist(
    val id: Long,
    val description: String,
    val isCompleted: Boolean,
    val position: Int
)
