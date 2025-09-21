package com.educost.kanone.presentation.screens.home

sealed interface HomeIntent {

    // Create Board
    data class CreateBoard(val boardName: String) : HomeIntent

    // Rename Board
    data class RenameBoardClicked(val boardId: Long) : HomeIntent
    data class OnConfirmRenameBoard(val newName: String) : HomeIntent
    data object OnCancelRenameBoard : HomeIntent

    // Delete Board
    data class DeleteBoardClicked(val boardId: Long) : HomeIntent
    data object OnConfirmDeleteBoard : HomeIntent
    data object OnCancelDeleteBoard : HomeIntent

    // Navigate
    data class NavigateToBoardScreen(val boardId: Long) : HomeIntent
    data object NavigateToSettingsScreen : HomeIntent

}