package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.BoardState
import com.educost.kanone.presentation.screens.board.model.CardUi
import com.educost.kanone.presentation.screens.board.model.ColumnUi
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.theme.KanOneTheme
import java.time.LocalDateTime

@Composable
fun BoardColumn(
    modifier: Modifier = Modifier,
    column: ColumnUi,
    state: BoardState,
    onIntent: (BoardIntent) -> Unit
) {
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            itemsIndexed(
                items = column.cards,
                key = { _, card -> card.id }
            ) { index, card ->
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
                        .fillMaxWidth(),
                    card = card,
                    onIntent = onIntent
                )
            }

            item {
                if (state.cardCreationState.columnId == column.id) {
                    AddCardTextField(
                        modifier = Modifier.fillMaxWidth(),
                        newCardTitle = state.cardCreationState.title ?: "",
                        onTitleChange = { onIntent(BoardIntent.OnCardTitleChange(it)) },
                        onConfirmCreateCard = { onIntent(BoardIntent.ConfirmCardCreation) }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        AddCardButton { onIntent(BoardIntent.StartCreatingCard(column.id)) }
                    }
                }
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
                            checklists = emptyList(),
                            attachments = emptyList(),
                            labels = emptyList(),
                            coordinates = Coordinates()
                        )
                    )
                ),
                state = BoardState(),
                onIntent = {}
            )
        }
    }
}