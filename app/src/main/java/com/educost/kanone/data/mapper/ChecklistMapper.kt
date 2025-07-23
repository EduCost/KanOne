package com.educost.kanone.data.mapper

import com.educost.kanone.data.model.entity.ChecklistEntity
import com.educost.kanone.domain.model.Checklist

fun ChecklistEntity.toChecklist() = Checklist(
    id = this.id,
    description = this.description,
    isCompleted = this.isCompleted,
    position = this.position
)

fun Checklist.toChecklistEntity(cardId: Long) = ChecklistEntity(
    id = this.id,
    description = this.description,
    position = this.position,
    isCompleted = this.isCompleted,
    cardId = cardId
)