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