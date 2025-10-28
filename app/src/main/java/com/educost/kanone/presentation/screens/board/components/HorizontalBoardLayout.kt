package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.screens.board.state.BoardUiState
import com.educost.kanone.presentation.screens.board.utils.setBoardCoordinates
import com.educost.kanone.presentation.screens.board.utils.setColumnCoordinates

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
        items(
            items = board.columns,
        ) { column ->
            val isDraggingColumn = state.dragState.isColumnBeingDragged(column.id)

            BoardColumn(
                modifier = Modifier
                    .setColumnCoordinates(
                        columnId = column.id,
                        onSetCoordinates = { onIntent(BoardIntent.OnSetCoordinates(it)) }
                    )
                    .then(
                        if (isDraggingColumn) {
                            Modifier
                                .graphicsLayer {
                                    colorFilter = ColorFilter.tint(Color.Gray)
                                    alpha = 0.05f
                                }
                        } else {
                            Modifier
                        }
                    ),
                column = column,
                state = state,
                onIntent = onIntent,
                sizes = board.sizes,
                showCardImages = board.showImages
            )
        }

        item {
            AddColumn(
                state = state,
                onIntent = onIntent,
                sizes = board.sizes
            )
        }
    }
}