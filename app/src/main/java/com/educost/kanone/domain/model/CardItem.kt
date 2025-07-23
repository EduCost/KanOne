package com.educost.kanone.domain.model

import com.educost.kanone.presentation.theme.Palette
import java.time.LocalDateTime

data class CardItem(
    val id: Long,
    val title: String,
    val description: String?,
    val position: Int,
    val color: Palette,
    val createdAt: LocalDateTime,
    val dueDate: LocalDateTime?,
    val thumbnailFileName: String?,
    val checkLists: List<Checklist>,
    val attachments: List<Attachment>,
    val labels: List<Label>,
)