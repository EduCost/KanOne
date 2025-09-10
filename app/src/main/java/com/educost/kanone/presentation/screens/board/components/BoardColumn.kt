package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.model.CardUi
import com.educost.kanone.presentation.screens.board.model.ColumnUi
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.screens.board.state.BoardState
import com.educost.kanone.presentation.theme.KanOneTheme
import java.time.LocalDateTime

@Composable
fun BoardColumn(
    modifier: Modifier = Modifier,
    column: ColumnUi,
    columnIndex: Int,
    state: BoardState,
    onIntent: (BoardIntent) -> Unit
) {

    var isAddingCardOnTop by remember(state.cardCreationState) {
        mutableStateOf(
            state.cardCreationState.columnId == column.id &&
                    !state.cardCreationState.isAppendingToEnd
        )
    }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(isAddingCardOnTop) {
        if (isAddingCardOnTop) {
            focusManager.clearFocus()
            focusManager.moveFocus(FocusDirection.Down)
        }
    }

    Column(
        modifier = modifier
            .width(300.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
    ) {
        ColumnHeader(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            column = column,
            state = state,
            onIntent = onIntent
        )

        LazyColumn(
            modifier = Modifier
                .onGloballyPositioned { layoutCoordinates ->
                    onIntent(
                        BoardIntent.SetColumnBodyCoordinates(
                            columnId = column.id,
                            coordinates = Coordinates(
                                position = layoutCoordinates.positionInRoot(),
                                width = layoutCoordinates.size.width,
                                height = layoutCoordinates.size.height
                            )
                        )
                    )
                },
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = column.listState
        ) {

            item {
                if (isAddingCardOnTop) {
                    AddCardTextField(
                        newCardTitle = state.cardCreationState.title ?: "",
                        onTitleChange = { onIntent(BoardIntent.OnCardTitleChange(it)) },
                        onConfirmCreateCard = { onIntent(BoardIntent.ConfirmCardCreation) }
                    )
                }
            }

            itemsIndexed(
                items = column.cards,
                key = { index, card -> card.id }
            ) { index, card ->

                val isDraggingCard = state.dragState.draggingCardIndex == index &&
                        state.dragState.selectedColumnIndex == columnIndex

                ColumnCard(
                    modifier = Modifier
                        .animateItem()
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
                            if (isDraggingCard) {
                                Modifier
                                    .graphicsLayer {
                                        colorFilter = ColorFilter.tint(Color.Gray)
                                        alpha = 0.05f
                                    }
                            } else {
                                Modifier
                            }
                        )
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onIntent(BoardIntent.OnCardClick(card.id)) }
                            )
                        },
                    card = card,
                )
            }

            item {
                AddCard(
                    state = state,
                    onIntent = onIntent,
                    column = column
                )
            }
        }
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
                            thumbnailFileName = null,
                            tasks = emptyList(),
                            attachments = emptyList(),
                            labels = emptyList(),
                            coordinates = Coordinates()
                        )
                    )
                ),
                state = BoardState(),
                columnIndex = 0,
                onIntent = {}
            )
        }
    }
}