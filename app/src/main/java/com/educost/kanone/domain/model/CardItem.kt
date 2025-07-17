package com.educost.kanone.domain.model

import com.educost.kanone.presentation.theme.Palette
import java.time.LocalDateTime

data class CardItem(
    val id: Long,
    val title: String,
    val position: Int,
    val description: String?,
    val createdAt: LocalDateTime,
    val dueDate: LocalDateTime?,
    val color: Palette,
    val thumbnailFileName: String?,
    val columnId: Long
)