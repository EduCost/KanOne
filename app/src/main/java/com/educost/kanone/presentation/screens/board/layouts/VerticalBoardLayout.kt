package com.educost.kanone.presentation.screens.board.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.components.AddColumn
import com.educost.kanone.presentation.screens.board.components.boardColumnList
import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.screens.board.state.BoardUiState
import com.educost.kanone.presentation.screens.board.utils.setBoardCoordinates

@Composable
fun VerticalBoardLayout(
    modifier: Modifier = Modifier,
    board: BoardUi,
    state: BoardUiState,
    onIntent: (BoardIntent) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .setBoardCoordinates { onIntent(BoardIntent.OnSetCoordinates(it)) }
            .fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = board.listState
    ) {

        boardColumnList(
            board = board,
            state = state,
            onIntent = onIntent,
            isOnVerticalLayout = true
        )

        item {
            AddColumn(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                onIntent = onIntent,
            )
        }
    }
}