package com.educost.kanone.presentation.screens.board.model

import androidx.compose.foundation.lazy.LazyListState

data class BoardUi(
    val id: Long,
    val name: String,
    val columns: List<ColumnUi>,
    val coordinates: Coordinates = Coordinates(),
    val listState: LazyListState = LazyListState(),
    val sizes: BoardSizes = BoardSizes(),
    val showImages: Boolean = true,
    val isOnListView: Boolean = false,
)
