package com.educost.kanone.presentation.screens.board.mapper

import com.educost.kanone.domain.model.Board
import com.educost.kanone.presentation.screens.board.model.BoardSizes
import com.educost.kanone.presentation.screens.board.model.BoardUi

fun BoardUi.toBoard() = Board(
    id = this.id,
    name = this.name,
    columns = this.columns.map { it.toKanbanColumn() },
    zoomPercentage = this.sizes.zoomPercentage,
    showImages = this.showImages,
    isOnVerticalLayout = this.isOnVerticalLayout
)

fun Board.toBoardUi() = BoardUi(
    id = this.id,
    name = this.name,
    columns = this.columns.map { it.toColumnUi() },
    sizes = BoardSizes(zoomPercentage = this.zoomPercentage),
    showImages = this.showImages,
    isOnVerticalLayout = this.isOnVerticalLayout
)

fun Board.toBoardUi(currentBoard: BoardUi): BoardUi {
    return this.toBoardUi().copy(
        columns = this.columns.map { column ->
            val currentColumn = currentBoard.columns.find { it.id == column.id }

            if (currentColumn != null)
                column.toColumnUi(currentColumn)
            else
                column.toColumnUi()
        }.sortedBy { it.position },
        coordinates = currentBoard.coordinates,
        listState = currentBoard.listState
    )
}