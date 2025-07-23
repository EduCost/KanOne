package com.educost.kanone.data.mapper

import com.educost.kanone.data.model.entity.BoardEntity
import com.educost.kanone.data.model.relation.BoardWithColumns
import com.educost.kanone.domain.model.Board

fun BoardEntity.toBoard(): Board = Board(
    id = this.id,
    name = this.name,
    columns = emptyList()
)

fun Board.toBoardEntity(): BoardEntity = BoardEntity(
    id = this.id,
    name = this.name,
)

fun BoardWithColumns.toBoard(): Board = Board(
    id = this.board.id,
    name = this.board.name,
    columns = this.columns.map { it.toKanbanColumn() }
)