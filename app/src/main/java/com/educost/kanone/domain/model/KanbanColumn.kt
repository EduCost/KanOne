package com.educost.kanone.domain.model

import com.educost.kanone.presentation.theme.Palette

data class KanbanColumn(
    val id: Long,
    val name: String,
    val position: Int,
    val color: Palette,
    val boardId: Long
)
