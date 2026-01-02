package com.educost.kanone.presentation.screens.board.mapper

import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.presentation.screens.board.model.CardUi

fun CardUi.toCardItem() = CardItem(
    id = this.id,
    title = this.title,
    description = this.description ?: "",
    position = this.position,
    color = this.color,
    createdAt = this.createdAt,
    dueDate = this.dueDate,
    coverFileName = this.coverFileName,
    tasks = this.tasks,
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
    coverFileName = this.coverFileName,
    tasks = this.tasks,
    attachments = this.attachments,
    labels = this.labels,
)

fun CardItem.toCardUi(currentCard: CardUi): CardUi {
    return this.toCardUi().copy(
        coordinates = currentCard.coordinates
    )
}