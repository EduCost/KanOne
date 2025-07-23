package com.educost.kanone.data.mapper

import com.educost.kanone.data.model.entity.ColumnEntity
import com.educost.kanone.data.model.relation.ColumnsWithCards
import com.educost.kanone.domain.model.KanbanColumn

fun ColumnEntity.toKanbanColumn(): KanbanColumn = KanbanColumn(
    id = this.id,
    name = this.name,
    position = this.position,
    color = this.color,
    cards = emptyList()
)

fun KanbanColumn.toColumnEntity(boardId: Long): ColumnEntity = ColumnEntity(
    id = this.id,
    name = this.name,
    position = this.position,
    color = this.color,
    boardId = boardId
)

fun ColumnsWithCards.toKanbanColumn(): KanbanColumn = KanbanColumn(
    id = this.column.id,
    name = this.column.name,
    position = this.column.position,
    color = this.column.color,
    cards = this.cards.map { it.toCardItem() }
)