package com.educost.kanone.domain.model

data class Checklist(
    val id: Long,
    val description: String,
    val position: Int,
    val isCompleted: Boolean,
    val cardId: Long
)
