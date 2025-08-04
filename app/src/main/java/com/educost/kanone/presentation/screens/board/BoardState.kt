package com.educost.kanone.presentation.screens.board

import androidx.compose.ui.geometry.Offset
import com.educost.kanone.presentation.screens.board.components.BoardAppBarType
import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.screens.board.model.CardUi
import com.educost.kanone.presentation.screens.board.model.ColumnUi

data class BoardState(
    val board: BoardUi? = null,
    val isLoading: Boolean = false,
    val topBarType: BoardAppBarType = BoardAppBarType.DEFAULT,
    val creatingColumnName: String? = null,
    val cardCreationState: CardCreationState = CardCreationState(),

    val dragState: DragState = DragState(),
)

data class CardCreationState(
    val title: String? = null,
    val columnId: Long? = null,
)

data class DragState(
    val itemOffset: Offset = Offset.Zero,
    val draggingCard: CardUi? = null,
    val draggingCardIndex: Int? = null,
    val selectedColumn: ColumnUi? = null,
    val selectedColumnIndex: Int? = null,
    val draggingColumn: ColumnUi? = null,
)