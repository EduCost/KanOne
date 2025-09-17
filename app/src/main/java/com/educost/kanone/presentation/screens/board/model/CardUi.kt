package com.educost.kanone.presentation.screens.board.model

import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.domain.model.Task
import com.educost.kanone.domain.model.Label
import java.time.LocalDateTime

data class CardUi(
    val id: Long,
    val title: String,
    val description: String?,
    val position: Int,
    val color: Int?,
    val createdAt: LocalDateTime,
    val dueDate: LocalDateTime?,
    val coverFileName: String?,
    val tasks: List<Task>,
    val attachments: List<Attachment>,
    val labels: List<Label>,
    val coordinates: Coordinates
)
