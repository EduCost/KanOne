package com.educost.kanone.data.mapper

import com.educost.kanone.data.model.entity.BoardEntity
import com.educost.kanone.data.model.relation.BoardWithColumns
import com.educost.kanone.domain.model.Board

fun BoardEntity.toBoard(): Board = Board(
    id = this.id,
    name = this.name,
    columns = emptyList(),
    zoomPercentage = this.zoomPercentage,
    showImages = this.showImages,
)

fun Board.toBoardEntity(): BoardEntity = BoardEntity(
    id = this.id,
    name = this.name,
    zoomPercentage = this.zoomPercentage,
    showImages = this.showImages,
)

fun BoardWithColumns.toBoard(): Board = Board(
    id = this.board.id,
    name = this.board.name,
    columns = this.columns.map { it.toKanbanColumn() },
    zoomPercentage = this.board.zoomPercentage,
    showImages = this.board.showImages,
)