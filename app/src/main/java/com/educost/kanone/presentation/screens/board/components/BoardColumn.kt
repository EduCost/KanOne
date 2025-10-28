package com.educost.kanone.presentation.screens.board.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.model.BoardSizes
import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.screens.board.model.CardUi
import com.educost.kanone.presentation.screens.board.model.ColumnUi
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.screens.board.state.BoardUiState
import com.educost.kanone.presentation.screens.board.utils.dragPlaceholder
import com.educost.kanone.presentation.screens.board.utils.setColumnCoordinates
import com.educost.kanone.presentation.screens.board.utils.setColumnListCoordinates
import com.educost.kanone.presentation.theme.KanOneTheme
import java.time.LocalDateTime

fun LazyListScope.boardColumnList(
    board: BoardUi,
    state: BoardUiState,
    onIntent: (BoardIntent) -> Unit,
    isOnVerticalLayout: Boolean
) {
    items(items = board.columns) { column ->

        val isDraggingColumn = state.dragState.isColumnBeingDragged(column.id)

        BoardColumn(
            modifier = Modifier
                .then(other =
                    if (isOnVerticalLayout) Modifier.fillMaxWidth()
                    else Modifier
                )
                .setColumnCoordinates(
                    columnId = column.id,
                    onSetCoordinates = { onIntent(BoardIntent.OnSetCoordinates(it)) }
                )
                .dragPlaceholder(isDraggingColumn),
            column = column,
            state = state,
            onIntent = onIntent,
            sizes =
                if (isOnVerticalLayout) BoardSizes()
                else board.sizes,
            showCardImages = board.showImages,
            isOnVerticalLayout = isOnVerticalLayout
        )
    }
}

@Composable
fun BoardColumn(
    modifier: Modifier = Modifier,
    column: ColumnUi,
    state: BoardUiState,
    onIntent: (BoardIntent) -> Unit,
    sizes: BoardSizes = BoardSizes(),
    isOnVerticalLayout: Boolean = false,
    showCardImages: Boolean
) {
    Column(
        modifier = modifier
            .width(sizes.columnWidth)
            .clip(sizes.columnShape)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
    ) {

        ColumnHeader(
            modifier = Modifier
                .padding(sizes.columnHeaderPadding)
                .fillMaxWidth(),
            column = column,
            state = state,
            onIntent = onIntent,
            sizes = sizes,
            isOnVerticalLayout = isOnVerticalLayout
        )

        AnimatedContent(isOnVerticalLayout && !column.isExpanded) { isCollapsed ->

            if (isCollapsed) CollapsedCards(cardAmount = column.cards.size)

            else LazyColumn(
                modifier = Modifier
                    .then(other =
                        if (isOnVerticalLayout) Modifier.heightIn(max = 87000.dp)
                        else Modifier
                    )
                    .setColumnListCoordinates(
                        columnId = column.id,
                        onSetCoordinates = { onIntent(BoardIntent.OnSetCoordinates(it)) }
                    )
                    .padding(sizes.columnListPadding),
                contentPadding = sizes.columnListPaddingValues,
                verticalArrangement = Arrangement.spacedBy(sizes.columnListSpaceBy),
                state = column.listState
            ) {

                addingCardOnTheTop(
                    column = column,
                    state = state,
                    onIntent = onIntent,
                    sizes = sizes
                )

                columnCardList(
                    column = column,
                    state = state,
                    onIntent = onIntent,
                    showCardImages = showCardImages,
                    sizes = sizes
                )

                addCard(
                    state = state,
                    onIntent = onIntent,
                    column = column,
                    sizes = sizes
                )
            }
        }
    }
}


@Composable
fun CollapsedCards(
    modifier: Modifier = Modifier,
    cardAmount: Int
) {
    Column(
        modifier = modifier
            .clip(shape = RoundedCornerShape(
                bottomStart = 12.dp,
                bottomEnd = 12.dp
            ))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = pluralStringResource(
                id = R.plurals.board_column_card_amount,
                count = cardAmount,
                cardAmount
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@PreviewLightDark
@Composable
private fun BoardColumnPreview() {
    KanOneTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            BoardColumn(
                modifier = Modifier.padding(34.dp),
                column = ColumnUi(
                    id = 0,
                    name = "Column name",
                    position = 0,
                    color = null,
                    cards = listOf(
                        CardUi(
                            id = 0,
                            title = "Card title",
                            position = 0,
                            color = null,
                            description = null,
                            dueDate = null,
                            createdAt = LocalDateTime.now(),
                            coverFileName = null,
                            tasks = emptyList(),
                            attachments = emptyList(),
                            labels = emptyList(),
                            coordinates = Coordinates()
                        )
                    )
                ),
                state = BoardUiState(),
                onIntent = {},
                showCardImages = true
            )
        }
    }
}