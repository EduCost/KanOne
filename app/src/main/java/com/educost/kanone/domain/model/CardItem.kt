package com.educost.kanone.domain.model

import java.time.LocalDateTime

data class CardItem(
    val id: Long,
    val title: String,
    val description: String?,
    val position: Int,
    val color: Int?,
    val createdAt: LocalDateTime,
    val dueDate: LocalDateTime?,
    val thumbnailFileName: String?,
    val checklists: List<Checklist>,
    val attachments: List<Attachment>,
    val labels: List<Label>,
)