package com.educost.kanone.presentation.screens.board

import androidx.compose.ui.geometry.Offset
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.screens.board.utils.CardOrder
import com.educost.kanone.presentation.screens.board.utils.OrderType

sealed interface BoardIntent {
    data class ObserveBoard(val boardId: Long) : BoardIntent

    // Drag and drop
    data class OnDragStart(val offset: Offset) : BoardIntent
    data class OnDrag(val position: Offset) : BoardIntent
    data object OnDragStop : BoardIntent

    // Create Card
    data class StartCreatingCard(val columnId: Long, val isAppendingToEnd: Boolean) : BoardIntent
    data class OnCardTitleChange(val title: String) : BoardIntent
    data object ConfirmCardCreation : BoardIntent
    data object CancelCardCreation : BoardIntent

    // Create column
    data object StartCreatingColumn : BoardIntent
    data class OnColumnNameChanged(val name: String) : BoardIntent
    data object CancelColumnCreation : BoardIntent
    data object ConfirmColumnCreation : BoardIntent

    // Edit Column
    data class OnEditColumnNameChange(val name: String) : BoardIntent
    data object CancelColumnRename : BoardIntent
    data object ConfirmColumnRename : BoardIntent


    // Column dropdown menu
    data class OpenColumnDropdownMenu(val columnId: Long) : BoardIntent
    data object CloseColumnDropdownMenu : BoardIntent
    data class OnRenameColumnClicked(val columnId: Long) : BoardIntent
    data class OnDeleteColumnClicked(val columnId: Long) : BoardIntent
    data class OnOrderByClicked(
        val columnId: Long,
        val orderType: OrderType,
        val cardOrder: CardOrder
    ) : BoardIntent


    // Set coordinates
    data class SetBoardCoordinates(val coordinates: Coordinates) : BoardIntent
    data class SetColumnHeaderCoordinates(val columnId: Long, val coordinates: Coordinates) :
        BoardIntent

    data class SetColumnBodyCoordinates(val columnId: Long, val coordinates: Coordinates) :
        BoardIntent

    data class SetColumnCoordinates(val columnId: Long, val coordinates: Coordinates) : BoardIntent
    data class SetCardCoordinates(
        val cardId: Long,
        val columnId: Long,
        val coordinates: Coordinates
    ) : BoardIntent

}