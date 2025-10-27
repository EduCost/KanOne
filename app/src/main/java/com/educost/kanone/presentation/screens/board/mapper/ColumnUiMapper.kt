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