package com.educost.kanone.presentation.screens.board

import com.educost.kanone.presentation.screens.board.components.BoardAppBarType
import com.educost.kanone.presentation.screens.board.model.BoardUi

data class BoardState(
    val board: BoardUi? = null,
    val isLoading: Boolean = false,
    val topBarType: BoardAppBarType = BoardAppBarType.DEFAULT,

    val creatingColumnName: String? = null,

    val cardCreationState: CardCreationState = CardCreationState()
)

data class CardCreationState(
    val title: String? = null,
    val columnId: Long? = null,
)
