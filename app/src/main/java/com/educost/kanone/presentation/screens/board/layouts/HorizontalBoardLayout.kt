package com.educost.kanone.presentation.screens.board.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.components.AddColumn
import com.educost.kanone.presentation.screens.board.components.boardColumnList
import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.screens.board.state.BoardUiState
import com.educost.kanone.presentation.screens.board.utils.setBoardCoordinates

@Composable
fun HorizontalBoardLayout(
    modifier: Modifier = Modifier,
    board: BoardUi,
    state: BoardUiState,
    onIntent: (BoardIntent) -> Unit
) {

    val contentPadding = remember(state.isOnFullScreen, board.sizes) {
        if (state.isOnFullScreen) board.sizes.columnFullScreenPaddingValues
        else board.sizes.columnPaddingValues
    }

    LazyRow(
        modifier = modifier
            .setBoardCoordinates { onIntent(BoardIntent.OnSetCoordinates(it)) }
            .fillMaxSize(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(board.sizes.columnsSpaceBy),
        state = board.listState
    ) {

        boardColumnList(
            board = board,
            state = state,
            onIntent = onIntent,
            isOnVerticalLayout = false
        )

        item {
            AddColumn(
                state = state,
                onIntent = onIntent,
                sizes = board.sizes
            )
        }
    }
}