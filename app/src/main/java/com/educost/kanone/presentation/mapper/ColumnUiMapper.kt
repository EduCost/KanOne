package com.educost.kanone.presentation.mapper

import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.presentation.model.ColumnUi

fun ColumnUi.toKanbanColumn() = KanbanColumn(
    id = this.id,
    name = this.name,
    position = this.position,
    color = this.color,
    cards = this.cards.map { it.toCardItem() }
)

fun KanbanColumn.toColumnUi() = ColumnUi(
    id = this.id,
    name = this.name,
    position = this.position,
    color = this.color,
    cards = this.cards.map { it.toCardUi() }
)