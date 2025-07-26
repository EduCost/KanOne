package com.educost.kanone.presentation.screens.board

import com.educost.kanone.presentation.model.BoardUi
import com.educost.kanone.presentation.screens.board.components.BoardAppBarType

data class BoardState(
    val board: BoardUi? = null,
    val isLoading: Boolean = false,
    val topBarType: BoardAppBarType = BoardAppBarType.DEFAULT
)
