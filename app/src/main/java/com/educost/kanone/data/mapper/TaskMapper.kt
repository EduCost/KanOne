package com.educost.kanone.data.mapper

import com.educost.kanone.data.model.entity.TaskEntity
import com.educost.kanone.domain.model.Task

fun TaskEntity.toTask() = Task(
    id = this.id,
    description = this.description,
    isCompleted = this.isCompleted,
    position = this.position
)

fun Task.toTaskEntity(cardId: Long) = TaskEntity(
    id = this.id,
    description = this.description,
    position = this.position,
    isCompleted = this.isCompleted,
    cardId = cardId
)