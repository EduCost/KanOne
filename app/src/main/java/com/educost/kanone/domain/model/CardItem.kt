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
    val coverFileName: String? = null,
    val priority: CardPriority = CardPriority.NORMAL,
    val tasks: List<Task> = emptyList(),
    val attachments: List<Attachment> = emptyList(),
    val labels: List<Label> = emptyList(),
)

enum class CardPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}