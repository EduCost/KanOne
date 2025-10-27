package com.educost.kanone.presentation.screens.board.state

import androidx.compose.ui.geometry.Offset
import com.educost.kanone.presentation.screens.board.model.CardUi
import com.educost.kanone.presentation.screens.board.model.ColumnUi

data class DragState(
    val itemBeingDraggedOffset: Offset = Offset.Zero,

    val cardBeingDragged: CardUi? = null,
    val cardBeingDraggedIndex: Int? = null,
    val cardBeingDraggedColumn: ColumnUi? = null,
    val cardBeingDraggedColumnIndex: Int? = null,

    val columnBeingDragged: ColumnUi? = null,
    val columnBeingDraggedIndex: Int? = null,
) {

    fun isDraggingColumn(): Boolean {
        return columnBeingDragged != null && columnBeingDraggedIndex != null
    }

    fun isDraggingCard(): Boolean {
        return cardBeingDragged != null &&
                cardBeingDraggedIndex != null &&
                cardBeingDraggedColumn != null &&
                cardBeingDraggedColumnIndex != null
    }

    fun isColumnBeingDragged(columnId: Long) = columnBeingDragged?.id == columnId

    fun isCardBeingDragged(cardId: Long) = cardBeingDragged?.id == cardId


    fun isActivelyDragging(): Boolean = this != DragState()

}