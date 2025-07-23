package com.educost.kanone.presentation.screens.board

import com.educost.kanone.domain.model.Attachment
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.Checklist
import com.educost.kanone.domain.model.Label
import com.educost.kanone.presentation.model.CardUi
import com.educost.kanone.presentation.model.ColumnUi
import com.educost.kanone.presentation.screens.board.components.BoardAppBarType

data class BoardState(
    val board: Board? = null,
    val isLoading: Boolean = false,
    val topBarType: BoardAppBarType = BoardAppBarType.DEFAULT
)
