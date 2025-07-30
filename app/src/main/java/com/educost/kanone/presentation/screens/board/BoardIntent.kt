package com.educost.kanone.presentation.screens.board

import com.educost.kanone.presentation.screens.board.model.Coordinates

sealed interface BoardIntent {
    data class ObserveBoard(val boardId: Long) : BoardIntent

    // Create Card
    data class StartCreatingCard(val columnId: Long) : BoardIntent
    data class OnCardTitleChange(val title: String) : BoardIntent
    data object ConfirmCardCreation : BoardIntent
    data object CancelCardCreation : BoardIntent

    // Create column
    data object StartCreatingColumn : BoardIntent
    data class OnColumnNameChanged(val name: String) : BoardIntent
    data object CancelColumnCreation : BoardIntent
    data object ConfirmColumnCreation : BoardIntent


    // Set coordinates
    data class SetBoardCoordinates(val coordinates: Coordinates) : BoardIntent
    data class SetColumnHeaderCoordinates(val columnId: Long, val coordinates: Coordinates) : BoardIntent
    data class SetColumnBodyCoordinates(val columnId: Long, val coordinates: Coordinates) : BoardIntent
    data class SetCardCoordinates(val cardId: Long, val columnId: Long, val coordinates: Coordinates) : BoardIntent

}