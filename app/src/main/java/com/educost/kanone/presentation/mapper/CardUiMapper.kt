package com.educost.kanone.presentation.mapper

import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.presentation.model.CardUi
import com.educost.kanone.presentation.model.Coordinates

fun CardUi.toCardItem() = CardItem(
    id = this.id,
    title = this.title,
    description = this.description,
    position = this.position,
    color = this.color,
    createdAt = this.createdAt,
    dueDate = this.dueDate,
    thumbnailFileName = this.thumbnailFileName,
    checklists = this.checklists,
    attachments = this.attachments,
    labels = this.labels
)

fun CardItem.toCardUi() = CardUi(
    id = this.id,
    title = this.title,
    description = this.description,
    position = this.position,
    color = this.color,
    createdAt = this.createdAt,
    dueDate = this.dueDate,
    thumbnailFileName = this.thumbnailFileName,
    checklists = this.checklists,
    attachments = this.attachments,
    labels = this.labels,
    coordinates = Coordinates()
)