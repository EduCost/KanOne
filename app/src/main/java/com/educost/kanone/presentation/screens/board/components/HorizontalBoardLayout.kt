package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.screens.board.state.BoardState

@Composable
fun HorizontalBoardLayout(
    modifier: Modifier = Modifier,
    board: BoardUi,
    state: BoardState,
    onIntent: (BoardIntent) -> Unit
) {

    val contentPadding = remember(state.isOnFullScreen, board.sizes) {
        if (state.isOnFullScreen) board.sizes.columnFullScreenPaddingValues
        else board.sizes.columnPaddingValues
    }

    LazyRow(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                onIntent(
                    BoardIntent.SetBoardCoordinates(
                        coordinates = Coordinates(
                            position = layoutCoordinates.positionInRoot(),
                            width = layoutCoordinates.size.width,
                            height = layoutCoordinates.size.height
                        )
                    )
                )
            }
            .fillMaxSize(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(board.sizes.columnsSpaceBy),
        state = board.listState
    ) {
        itemsIndexed(
            items = board.columns,
            key = { index, column -> "${column.id}_$index" }
        ) { index, column ->
            val isDraggingColumn = state.dragState.draggingColumn?.id == column.id
            BoardColumn(
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        onIntent(
                            BoardIntent.SetColumnCoordinates(
                                columnId = column.id,
                                coordinates = Coordinates(
                                    position = layoutCoordinates.positionInRoot(),
                                    width = layoutCoordinates.size.width,
                                    height = layoutCoordinates.size.height
                                )
                            )
                        )
                    }
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
                columnIndex = index,
                state = state,
                onIntent = onIntent,
                sizes = board.sizes
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