package com.educost.kanone.presentation.model

import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.domain.model.Checklist
import com.educost.kanone.domain.model.Label
import com.educost.kanone.presentation.theme.Palette
import java.time.LocalDateTime

data class CardUi(
    val id: Long,
    val title: String,
    val description: String?,
    val position: Int,
    val color: Palette,
    val createdAt: LocalDateTime,
    val dueDate: LocalDateTime?,
    val thumbnailFileName: String?,
    val columnId: Long,
    val coordinates: Coordinates
)
