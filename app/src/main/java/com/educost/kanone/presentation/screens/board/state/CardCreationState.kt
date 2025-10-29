package com.educost.kanone.presentation.screens.board.state

data class CardCreationState(
    val isAppendingToEnd: Boolean = false,
    val title: String? = null,
    val columnId: Long? = null,
)