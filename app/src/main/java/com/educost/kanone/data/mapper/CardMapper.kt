package com.educost.kanone.data.mapper

import com.educost.kanone.data.model.entity.CardEntity
import com.educost.kanone.domain.model.CardItem

fun CardEntity.toCardItem() = CardItem(
    id = this.id,
    title = this.title,
    description = this.description,
    position = this.position,
    color = this.color,
    createdAt = this.createdAt,
    dueDate = this.dueDate,
    thumbnailFileName = this.thumbnailFileName,
    columnId = this.columnId
)

fun CardItem.toCardEntity() = CardEntity(
    id = this.id,
    title = this.title,
    position = this.position,
    description = this.description,
    createdAt = this.createdAt,
    dueDate = this.dueDate,
    color = this.color,
    thumbnailFileName = this.thumbnailFileName,
    columnId = this.columnId
)