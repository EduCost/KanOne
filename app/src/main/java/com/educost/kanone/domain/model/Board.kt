package com.educost.kanone.domain.model

data class Board(
    val id: Long,
    val name: String,
    val columns: List<KanbanColumn>
)