package com.educost.kanone.presentation.screens.home

import com.educost.kanone.domain.model.Board

sealed interface HomeIntent {
    data class CreateBoard(val board: Board) : HomeIntent
    data object ShowCreateBoardDialog : HomeIntent
    data object DismissCreateBoardDialog : HomeIntent
    data class OnNewBoardNameChange(val newBoardName: String) : HomeIntent
    data class NavigateToBoardScreen(val boardId: Long) : HomeIntent

}