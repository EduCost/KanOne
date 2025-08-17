package com.educost.kanone.domain.model

import java.time.LocalDateTime

data class CardItem(
    val id: Long,
    val title: String,
    val description: String? = null,
    val position: Int,
    val color: Int? = null,
    val createdAt: LocalDateTime,
    val dueDate: LocalDateTime? = null,
    val thumbnailFileName: String? = null,
    val checklists: List<Checklist> = emptyList(),
    val attachments: List<Attachment> = emptyList(),
    val labels: List<Label> = emptyList(),
)