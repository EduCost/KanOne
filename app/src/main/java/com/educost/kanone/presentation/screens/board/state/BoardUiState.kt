package com.educost.kanone.presentation.screens.board.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.geometry.Offset
import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.screens.board.model.CardUi
import com.educost.kanone.presentation.screens.board.model.ColumnUi
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType
import com.educost.kanone.presentation.screens.board.utils.ItemWithIndex

data class BoardUiState(
    val board: BoardUi? = null,
    val dragState: DragState = DragState(),
    val isOnFullScreen: Boolean = false,

    // Edit states
    val topBarType: BoardAppBarType = BoardAppBarType.DEFAULT,
    val activeDropdownColumnId: Long? = null,
    val creatingColumnName: String? = null,
    val columnEditState: ColumnEditState = ColumnEditState(),
    val cardCreationState: CardCreationState = CardCreationState(),
    val isBoardDropdownMenuExpanded: Boolean = false,
    val isRenamingBoard: Boolean = false,
    val isShowingDeleteBoardDialog: Boolean = false,
    val isChangingZoom: Boolean = false,
    val isModalSheetExpanded: Boolean = false,

    val center: Offset = Offset.Zero
) {

    // Variable used for disabling back button
    val hasEditStates = topBarType != BoardAppBarType.DEFAULT ||
            activeDropdownColumnId != null ||
            creatingColumnName != null ||
            columnEditState != ColumnEditState() ||
            cardCreationState != CardCreationState() ||
            isBoardDropdownMenuExpanded ||
            isRenamingBoard ||
            isShowingDeleteBoardDialog


    fun onDragStart(offset: Offset, isOnVerticalLayout: Boolean): BoardUiState {
        if (board == null) return this

        val targetColumn = findColumnWithIndex(
            isOnVerticalLayout = isOnVerticalLayout,
            offset = offset,
            columns = board.columns,
            lazyListState = board.listState
        ) ?: return this


        val isSelectingHeader = isHeaderPressed(targetColumn.item, offset.y)
        if (isSelectingHeader) {
            val newOffset = targetColumn.item.adjustOffsetToCenter(offset)
            return this.copy(
                dragState = dragState.copy(
                    itemBeingDraggedOffset = newOffset,
                    columnBeingDragged = targetColumn.item,
                    columnBeingDraggedIndex = targetColumn.index
                )
            )
        }


        val targetCard = findCardWithIndex(
            column = targetColumn.item,
            offsetY = offset.y,
        ) ?: return this

        val newOffset = targetCard.item.adjustOffsetToCenter(offset)


        return this.copy(
            dragState = dragState.copy(
                itemBeingDraggedOffset = newOffset,
                cardBeingDragged = targetCard.item,
                cardBeingDraggedIndex = targetCard.index,
                cardBeingDraggedColumn = targetColumn.item,
                cardBeingDraggedColumnIndex = targetColumn.index,
            )
        )
    }

    fun onDrag(offset: Offset?, isOnVerticalLayout: Boolean): BoardUiState {

        if (dragState.isDraggingColumn()) {
            return dragColumn(offset, isOnVerticalLayout)
        }

        if (dragState.isDraggingCard()) {
            return dragCard(offset, isOnVerticalLayout)
        }

        return this
    }

    private fun dragColumn(offset: Offset?, isOnVerticalLayout: Boolean): BoardUiState {
        if (board == null) return this

        val columns = board.columns
        val column = dragState.columnBeingDragged ?: return this
        val columnIndex = dragState.columnBeingDraggedIndex ?: return this

        val newCoordinates = getUpdatedCoordinates(column)

        val newOffset =
            if (offset != null) column.adjustOffsetToCenter(offset)
            else dragState.itemBeingDraggedOffset

        val newState = this.copy(
            dragState = dragState.copy(
                itemBeingDraggedOffset = newOffset,
                columnBeingDragged = if (newCoordinates == null) column else column.copy(coordinates = newCoordinates)
            ),
            board = if (newCoordinates == null) {
                this.board
            } else {
                this.board.copy(
                    columns = this.board.columns.map {
                        if (it.id == column.id) {
                            it.copy(coordinates = newCoordinates)
                        } else {
                            it
                        }
                    }
                )
            }
        )


        val columnCenter = column.getNewHeaderCenter(newOffset)


        val targetColumn = findColumnWithIndex(
            isOnVerticalLayout = isOnVerticalLayout,
            offset = columnCenter,
            columns = columns,
            lazyListState = board.listState
        ) ?: return newState

        if (targetColumn.index == columnIndex) return newState


        val targetColumnCenter = targetColumn.item.getCenter()


        if (shouldSwapColumns(
                isOnVerticalLayout = isOnVerticalLayout,
                currentColumnCenter = columnCenter,
                targetColumnCenter = targetColumnCenter,
                currentColumnIndex = columnIndex,
                targetColumnIndex = targetColumn.index
            )
        ) {
            val newColumns = columns.toMutableList().apply {
                add(targetColumn.index, removeAt(columnIndex))
            }

            return newState.copy(
                board = board.copy(columns = newColumns),
                dragState = dragState.copy(
                    columnBeingDraggedIndex = targetColumn.index,
                )
            )
        }

        return newState

    }

    private fun dragCard(offset: Offset?, isOnVerticalLayout: Boolean): BoardUiState {
        if (board == null) return this

        val currentCard = dragState.cardBeingDragged ?: return this
        val currentCardIndex = dragState.cardBeingDraggedIndex ?: return this
        val currentColumn = dragState.cardBeingDraggedColumn ?: return this
        val currentColumnIndex = dragState.cardBeingDraggedColumnIndex ?: return this


        val newOffset =
            if (offset != null) currentCard.adjustOffsetToCenter(offset)
            else dragState.itemBeingDraggedOffset

        val newState = this.copy(
            dragState = dragState.copy(itemBeingDraggedOffset = newOffset)
        )


        val cardCenter = currentCard.getNewCenter(newOffset)


        val targetColumn = findColumnWithIndex(
            isOnVerticalLayout = isOnVerticalLayout,
            offset = cardCenter,
            columns = board.columns,
            lazyListState = board.listState
        ) ?: return newState


        val shouldTransferCardToAnotherColumn = currentColumnIndex != targetColumn.index
        if (shouldTransferCardToAnotherColumn) {
            val newCardIndex = determineDropIndexInColumn(
                cardOffsetY = cardCenter.y,
                targetColumn = targetColumn.item
            )

            val currentColumnCards = currentColumn.cards.toMutableList().apply {
                remove(currentCard)
            }
            val targetColumnCards = targetColumn.item.cards.toMutableList().apply {
                add(newCardIndex, currentCard)
            }

            val newColumns = board.columns.toMutableList().apply {
                set(currentColumnIndex, currentColumn.copy(cards = currentColumnCards))
                set(targetColumn.index, targetColumn.item.copy(cards = targetColumnCards))
            }


            return this.copy(
                board = board.copy(columns = newColumns),
                dragState = dragState.copy(
                    itemBeingDraggedOffset = newOffset,
                    cardBeingDraggedColumn = targetColumn.item,
                    cardBeingDraggedIndex = newCardIndex,
                    cardBeingDraggedColumnIndex = targetColumn.index
                )
            )
        }


        val targetCard = findCardWithIndex(
            column = targetColumn.item,
            offsetY = cardCenter.y
        ) ?: return newState


        val targetCardCenterY = targetCard.item.getCenterY()


        if (shouldSwapCards(
                currentCardCenterY = cardCenter.y,
                targetCardCenterY = targetCardCenterY,
                currentCardIndex = currentCardIndex,
                targetCardIndex = targetCard.index
            )
        ) {
            val newColumns = board.columns.toMutableList()


            val newCards = newColumns[currentColumnIndex].cards.toMutableList().apply {
                add(
                    index = targetCard.index,
                    element = removeAt(currentCardIndex)
                )
            }.toList()


            val updatedColumn = newColumns[currentColumnIndex].copy(cards = newCards)
            newColumns[currentColumnIndex] = updatedColumn


            return this.copy(
                board = board.copy(columns = newColumns),
                dragState = dragState.copy(
                    itemBeingDraggedOffset = newOffset,
                    cardBeingDraggedIndex = targetCard.index
                )
            )
        }


        return newState
    }

    private fun shouldSwapColumns(
        isOnVerticalLayout: Boolean,
        currentColumnCenter: Offset,
        targetColumnCenter: Offset,
        currentColumnIndex: Int,
        targetColumnIndex: Int
    ): Boolean {

        val isCurrentIndexGreaterThanTargetIndex = currentColumnIndex > targetColumnIndex
        val isCurrentIndexLessThanTargetIndex = currentColumnIndex < targetColumnIndex

        if (isOnVerticalLayout) {
            val isCurrentColumnAboveTarget = currentColumnCenter.y < targetColumnCenter.y
            val isCurrentColumnBellowTarget = currentColumnCenter.y > targetColumnCenter.y

            val shouldMoveUp = isCurrentColumnAboveTarget && isCurrentIndexGreaterThanTargetIndex
            val shouldMoveDown = isCurrentColumnBellowTarget && isCurrentIndexLessThanTargetIndex

            return shouldMoveUp || shouldMoveDown
        } else {
            val isCurrentColumnAfterTarget = currentColumnCenter.x > targetColumnCenter.x
            val isCurrentColumnBeforeTarget = currentColumnCenter.x < targetColumnCenter.x

            val shouldMoveRight = isCurrentColumnAfterTarget && isCurrentIndexLessThanTargetIndex
            val shouldMoveLeft = isCurrentColumnBeforeTarget && isCurrentIndexGreaterThanTargetIndex

            return shouldMoveRight || shouldMoveLeft
        }


    }

    private fun shouldSwapCards(
        currentCardCenterY: Float,
        targetCardCenterY: Float,
        currentCardIndex: Int,
        targetCardIndex: Int,
    ): Boolean {
        val isCurrentCardBelowTarget = currentCardCenterY > targetCardCenterY
        val isCurrentCardAboveTarget = currentCardCenterY < targetCardCenterY

        val isCurrentIndexGreaterThanTargetIndex = currentCardIndex > targetCardIndex
        val isCurrentIndexLessThanTargetIndex = currentCardIndex < targetCardIndex

        val shouldMoveDown = isCurrentCardBelowTarget && isCurrentIndexLessThanTargetIndex
        val shouldMoveUp = isCurrentCardAboveTarget && isCurrentIndexGreaterThanTargetIndex

        return shouldMoveDown || shouldMoveUp
    }

    private fun findColumnWithIndex(
        isOnVerticalLayout: Boolean,
        offset: Offset,
        columns: List<ColumnUi>,
        lazyListState: LazyListState
    ): ItemWithIndex<ColumnUi>? {
        val columnsOnScreen = lazyListState.layoutInfo.visibleItemsInfo.map { it.index }

        if (isOnVerticalLayout) {

            val targetColumn = columns.filterIndexed { index, column ->
                val columnTop = column.coordinates.position.y
                val columnBottom = column.coordinates.position.y + column.coordinates.height
                val columnRange = columnTop..columnBottom

                offset.y in columnRange && index in columnsOnScreen
            }.firstOrNull() ?: return null

            val targetColumnIndex = columns
                .indexOfFirst { it.id == targetColumn.id }
                .takeIf { it != -1 }
                ?: return null

            return ItemWithIndex(targetColumn, targetColumnIndex)

        } else {

            val targetColumn = columns.filterIndexed { index, column ->
                val columnStart = column.coordinates.position.x
                val columnEnd = column.coordinates.position.x + column.coordinates.width
                val columnRange = columnStart..columnEnd

                offset.x in columnRange && index in columnsOnScreen
            }.firstOrNull() ?: return null

            val targetColumnIndex = columns
                .indexOfFirst { it.id == targetColumn.id }
                .takeIf { it != -1 }
                ?: return null

            return ItemWithIndex(targetColumn, targetColumnIndex)
        }
    }

    private fun findCardWithIndex(column: ColumnUi, offsetY: Float): ItemWithIndex<CardUi>? {
        val cardsOnScreen = column.listState.layoutInfo.visibleItemsInfo.map { it.index }

        val targetCard = column.cards.filterIndexed { index, card ->
            val cardStart = card.coordinates.position.y
            val cardEnd = card.coordinates.position.y + card.coordinates.height
            val cardRange = cardStart..cardEnd

            offsetY in cardRange && index in cardsOnScreen
        }.firstOrNull() ?: return null

        val targetCardIndex = column.cards
            .indexOfFirst { it.id == targetCard.id }
            .takeIf { it != -1 }
            ?: return null

        return ItemWithIndex(targetCard, targetCardIndex)
    }

    private fun isHeaderPressed(
        column: ColumnUi,
        offsetY: Float,
    ): Boolean {
        val headerTop = column.headerCoordinates.position.y
        val headerBottom = column.headerCoordinates.position.y + column.headerCoordinates.height
        return offsetY in headerTop..headerBottom
    }

    private fun determineDropIndexInColumn(
        cardOffsetY: Float,
        targetColumn: ColumnUi
    ): Int {
        val potentialTargetIndex = findCardWithIndex(
            column = targetColumn,
            offsetY = cardOffsetY
        )?.index ?: -1

        val targetNotFound = potentialTargetIndex == -1
        val columnHasCards = targetColumn.cards.isNotEmpty()

        if (targetNotFound && columnHasCards) {
            val lastCardInColumn = targetColumn.cards.last()
            val lastCardCenterY = lastCardInColumn.coordinates.position.y +
                    lastCardInColumn.coordinates.height / 2

            return if (cardOffsetY > lastCardCenterY) {
                targetColumn.cards.size
            } else {
                0
            }
        } else if (targetNotFound) { // Didn't found card and column has no cards
            return 0
        } else {
            return potentialTargetIndex
        }
    }

    private fun getUpdatedCoordinates(currentColumn: ColumnUi): Coordinates? {
        if (board == null) return null

        val updatedColumn = board.columns.find { it.id == currentColumn.id } ?: return null

        if (updatedColumn.coordinates != currentColumn.coordinates) {
            return updatedColumn.coordinates
        }

        return null
    }

}

data class CardCreationState(
    val isAppendingToEnd: Boolean = false,
    val title: String? = null,
    val columnId: Long? = null,
)

data class ColumnEditState(
    val editingColumnId: Long? = null,
    val isRenaming: Boolean = false,
    val newColumnName: String? = null,
    val isShowingColorPicker: Boolean = false,
)