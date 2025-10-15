package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        contentPadding = PaddingValues(start = 8.dp, bottom = 8.dp, end = 8.dp),
        state = board.listState
    ) {
        verticalViewBoardColumns(
            board = board,
            state = state,
            onIntent = onIntent
        )
    }
}

fun LazyListScope.verticalViewBoardColumns(
    board: BoardUi,
    state: BoardUiState,
    onIntent: (BoardIntent) -> Unit
) {
    board.columns.forEach { column ->

        item {
            ColumnHeader(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                    .padding(8.dp)
                    .padding(bottom = 4.dp)
                    .fillMaxWidth(),
                column = column,
                state = state,
                onIntent = onIntent,
            )
        }

        items(column.cards) { card ->
            val isTheLastCard = remember { card.id == column.cards.last().id }

            ColumnCard(
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        onIntent(
                            BoardIntent.SetCardCoordinates(
                                cardId = card.id,
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
                        if (isTheLastCard) {
                            Modifier.clip(
                                RoundedCornerShape(
                                    bottomStart = 12.dp,
                                    bottomEnd = 12.dp
                                )
                            )
                        } else {
                            Modifier
                        }
                    )
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(),
                card = card,
                showImage = board.showImages,
                onClick = { onIntent(BoardIntent.OnCardClick(card.id)) }
            )
        }

    }
}