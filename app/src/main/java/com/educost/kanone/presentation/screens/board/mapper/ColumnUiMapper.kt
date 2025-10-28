package com.educost.kanone.presentation.screens.board.mapper

import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.presentation.screens.board.model.ColumnUi

fun ColumnUi.toKanbanColumn() = KanbanColumn(
    id = this.id,
    name = this.name,
    position = this.position,
    color = this.color,
    cards = this.cards.map { it.toCardItem() },
    isExpanded = this.isExpanded
)

fun KanbanColumn.toColumnUi() = ColumnUi(
    id = this.id,
    name = this.name,
    position = this.position,
    color = this.color,
    cards = this.cards.map { it.toCardUi() },
    isExpanded = this.isExpanded
)

fun KanbanColumn.toColumnUi(currentColumn: ColumnUi): ColumnUi {
    return this.toColumnUi().copy(
        cards = this.cards.map { card ->
            val currentCard = currentColumn.cards.find { it.id == card.id }

            if (currentCard != null)
                card.toCardUi(currentCard)
            else
                card.toCardUi()
        }.sortedBy { it.position },
        coordinates = currentColumn.coordinates,
        listCoordinates = currentColumn.listCoordinates,
        headerCoordinates = currentColumn.headerCoordinates,
        listState = currentColumn.listState
    )
}