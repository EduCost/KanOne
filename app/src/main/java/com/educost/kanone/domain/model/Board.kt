package com.educost.kanone.domain.model

data class Board(
    val id: Long,
    val name: String,
    val columns: List<KanbanColumn>,

    // Settings
    val zoomPercentage: Float = 100f,
    val showImages: Boolean = true,
    val isOnVerticalLayout: Boolean = false
)