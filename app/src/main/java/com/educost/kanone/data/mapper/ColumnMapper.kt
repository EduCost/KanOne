package com.educost.kanone.data.mapper

import com.educost.kanone.data.model.entity.ColumnEntity
import com.educost.kanone.domain.model.KanbanColumn

fun ColumnEntity.toKanbanColumn(): KanbanColumn = KanbanColumn(
    id = this.id,
    name = this.name,
    position = this.position,
    color = this.color,
    boardId = this.boardId
)

fun KanbanColumn.toColumnEntity(): ColumnEntity = ColumnEntity(
    id = this.id,
    name = this.name,
    position = this.position,
    color = this.color,
    boardId = this.boardId
)