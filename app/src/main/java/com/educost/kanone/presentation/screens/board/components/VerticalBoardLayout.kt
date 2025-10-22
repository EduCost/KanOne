package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.screens.board.state.BoardUiState

@Composable
fun VerticalBoardLayout(
    modifier: Modifier = Modifier,
    board: BoardUi,
    state: BoardUiState,
    onIntent: (BoardIntent) -> Unit
) {
    LazyColumn(
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
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = board.listState
    ) {
        items(board.columns) { column ->

            val isDraggingColumn = state.dragState.isColumnBeingDragged(column.id)

            BoardColumn(
                modifier = Modifier
                    .fillMaxWidth()
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
                state = state,
                onIntent = onIntent,
                cardColumnHeight = 600
            )
        }
    }
}