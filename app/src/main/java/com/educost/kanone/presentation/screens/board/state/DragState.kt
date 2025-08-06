package com.educost.kanone.presentation.screens.board.state

import androidx.compose.ui.geometry.Offset
import com.educost.kanone.presentation.screens.board.model.CardUi
import com.educost.kanone.presentation.screens.board.model.ColumnUi

data class DragState(
    val itemOffset: Offset = Offset.Companion.Zero,
    val draggingCard: CardUi? = null,
    val draggingCardIndex: Int? = null,
    val selectedColumn: ColumnUi? = null,
    val selectedColumnIndex: Int? = null,
    val draggingColumn: ColumnUi? = null,
)