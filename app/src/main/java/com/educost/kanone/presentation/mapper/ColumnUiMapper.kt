package com.educost.kanone.presentation.mapper

import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.presentation.model.ColumnUi
import com.educost.kanone.presentation.theme.Palette
import kotlin.Long

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