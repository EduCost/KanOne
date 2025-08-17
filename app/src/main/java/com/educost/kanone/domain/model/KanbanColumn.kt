package com.educost.kanone.domain.model

data class KanbanColumn(
    val id: Long,
    val name: String,
    val position: Int,
    val color: Int? = null,
    val cards: List<CardItem> = emptyList()
)
