package com.educost.kanone.presentation.screens.board

import com.educost.kanone.presentation.model.Coordinates

sealed interface BoardIntent {
    data class ObserveBoard(val boardId: Long) : BoardIntent

    // Set coordinates
    data class SetBoardCoordinates(val coordinates: Coordinates) : BoardIntent
    data class SetColumnHeaderCoordinates(val columnId: Long, val coordinates: Coordinates) : BoardIntent
    data class SetColumnBodyCoordinates(val columnId: Long, val coordinates: Coordinates) : BoardIntent
    data class SetCardCoordinates(val cardId: Long, val columnId: Long, val coordinates: Coordinates) : BoardIntent

}