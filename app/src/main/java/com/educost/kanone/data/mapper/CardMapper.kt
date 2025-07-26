package com.educost.kanone.data.mapper

import com.educost.kanone.data.model.entity.CardEntity
import com.educost.kanone.data.model.relation.CardWithRelations
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
    checklists = emptyList(),
    attachments = emptyList(),
    labels = emptyList()
)

fun CardItem.toCardEntity(columnId: Long) = CardEntity(
    id = this.id,
    title = this.title,
    position = this.position,
    description = this.description,
    createdAt = this.createdAt,
    dueDate = this.dueDate,
    color = this.color,
    thumbnailFileName = this.thumbnailFileName,
    columnId = columnId
)

fun CardWithRelations.toCardItem() = CardItem(
    id = this.card.id,
    title = this.card.title,
    description = this.card.description,
    position = this.card.position,
    color = this.card.color,
    createdAt = this.card.createdAt,
    dueDate = this.card.dueDate,
    thumbnailFileName = this.card.thumbnailFileName,
    checklists = this.checklists.map { it.toChecklist() },
    attachments = this.attachments.map { it.toAttachment() },
    labels = this.labels.map { it.toLabel() }
)
