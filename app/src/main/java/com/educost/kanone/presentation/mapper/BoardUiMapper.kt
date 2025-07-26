package com.educost.kanone.presentation.mapper

import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.presentation.model.BoardUi
import kotlin.Long

fun BoardUi.toBoard() = Board(
    id = this.id,
    name = this.name,
    columns = this.columns.map { it.toKanbanColumn() }
)

fun Board.toBoardUi() = BoardUi(
    id = this.id,
    name = this.name,
    columns = this.columns.map { it.toColumnUi() }
)