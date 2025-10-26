package com.educost.kanone.presentation.screens.board.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.model.BoardSizes
import com.educost.kanone.presentation.screens.board.model.CardUi
import com.educost.kanone.presentation.screens.board.model.ColumnUi
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.screens.board.state.BoardUiState
import com.educost.kanone.presentation.theme.KanOneTheme
import java.time.LocalDateTime

@SuppressLint("UnusedContentLambdaTargetStateParameter")
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

        AnimatedContent(isOnVerticalLayout && !column.isExpanded) {isCollapsed ->
            if (isCollapsed) {
                CollapsedCards(cardAmount = column.cards.size)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .then(
                            other = if (isOnVerticalLayout)
                                Modifier.heightIn(max = 87000.dp)
                            else
                                Modifier
                        )
                        .onGloballyPositioned { layoutCoordinates ->
                            onIntent(
                                BoardIntent.SetColumnListCoordinates(
                                    columnId = column.id,
                                    coordinates = Coordinates(
                                        position = layoutCoordinates.positionInRoot(),
                                        width = layoutCoordinates.size.width,
                                        height = layoutCoordinates.size.height
                                    )
                                )
                            )
                        },
                    contentPadding = sizes.columnListPaddingValues,
                    verticalArrangement = Arrangement.spacedBy(sizes.columnListSpaceBy),
                    state = column.listState
                ) {

                    item {
                        if (isAddingCardOnTop) {
                            AddCardTextField(
                                newCardTitle = state.cardCreationState.title ?: "",
                                onTitleChange = { onIntent(BoardIntent.OnCardTitleChange(it)) },
                                onConfirmCreateCard = { onIntent(BoardIntent.ConfirmCardCreation) },
                                sizes = sizes
                            )
                        }
                    }

                    itemsIndexed(
                        items = column.cards,
                        key = { index, card -> card.id }
                    ) { index, card ->

                        val isDraggingCard = state.dragState.isCardBeingDragged(card.id)

                        ColumnCard(
                            modifier = Modifier
                                .then(
                                    if (!state.isChangingZoom) Modifier.animateItem()
                                    else Modifier
                                )
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
                                .fillMaxWidth(),
                            card = card,
                            showImage = showCardImages,
                            sizes = sizes,
                            onClick = { onIntent(BoardIntent.OnCardClick(card.id)) }
                        )
                    }

                    item {
                        AddCard(
                            state = state,
                            onIntent = onIntent,
                            column = column,
                            sizes = sizes
                        )
                    }
                }
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
            .clip(
                RoundedCornerShape(
                    bottomStart = 12.dp,
                    bottomEnd = 12.dp
                )
            )
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