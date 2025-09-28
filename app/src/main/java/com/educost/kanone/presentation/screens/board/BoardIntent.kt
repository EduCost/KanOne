package com.educost.kanone.presentation.screens.board

import androidx.compose.ui.geometry.Offset
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.screens.board.utils.CardOrder
import com.educost.kanone.presentation.screens.board.utils.OrderType

sealed interface BoardIntent {
    data class ObserveBoard(val boardId: Long) : BoardIntent


    // Rename Board
    data object OnRenameBoardClicked : BoardIntent
    data class ConfirmBoardRename(val newName: String) : BoardIntent
    data object CancelBoardRename : BoardIntent


    // Delete Board
    data object OnDeleteBoardClicked : BoardIntent
    data object ConfirmBoardDeletion : BoardIntent
    data object CancelBoardDeletion : BoardIntent


    /*  Board Settings  */
    data object OpenBoardSettings : BoardIntent
    data object CloseBoardSettings : BoardIntent
    data object ToggleShowImages : BoardIntent

    // Zoom
    data class OnZoomChange(val zoomChange: Float, val scrollChange: Float) : BoardIntent
    data class SetZoom(val zoomValue: Float) : BoardIntent

    // Full Screen
    data object EnterFullScreen : BoardIntent
    data object ExitFullScreen : BoardIntent
    /*  Board Settings  */


    // Create column
    data object StartCreatingColumn : BoardIntent
    data class OnColumnNameChanged(val name: String) : BoardIntent
    data object CancelColumnCreation : BoardIntent
    data object ConfirmColumnCreation : BoardIntent


    // Column dropdown menu
    data class OpenColumnDropdownMenu(val columnId: Long) : BoardIntent
    data object CloseColumnDropdownMenu : BoardIntent
    data class OnDeleteColumnClicked(val columnId: Long) : BoardIntent
    data class OnOrderByClicked(
        val columnId: Long,
        val orderType: OrderType,
        val cardOrder: CardOrder
    ) : BoardIntent


    /*  Edit Column  */
    // Rename Column
    data class OnRenameColumnClicked(val columnId: Long) : BoardIntent
    data class OnEditColumnNameChange(val name: String) : BoardIntent
    data object CancelColumnRename : BoardIntent
    data object ConfirmColumnRename : BoardIntent

    // Column color
    data class StartEditingColumnColor(val columnId: Long) : BoardIntent
    data object CancelColumnColorEdit : BoardIntent
    data class ConfirmColumnColorEdit(val newColor: Int) : BoardIntent
    /*  Edit Column  */


    // Create Card
    data class StartCreatingCard(val columnId: Long, val isAppendingToEnd: Boolean) : BoardIntent
    data class OnCardTitleChange(val title: String) : BoardIntent
    data object ConfirmCardCreation : BoardIntent
    data object CancelCardCreation : BoardIntent


    /*  Others  */
    data object OnBackPressed : BoardIntent

    // Navigation
    data object OnNavigateBack : BoardIntent
    data class OnCardClick(val cardId: Long) : BoardIntent
    data object NavigateToSettings : BoardIntent

    // App bar dropdown menu
    data object OpenBoardDropdownMenu : BoardIntent
    data object CloseBoardDropdownMenu : BoardIntent
    /*  Others  */


    // Drag and drop
    data class OnDragStart(val offset: Offset) : BoardIntent
    data class OnDrag(val position: Offset) : BoardIntent
    data object OnDragStop : BoardIntent


    // Set coordinates
    data class SetBoardCoordinates(val coordinates: Coordinates) : BoardIntent
    data class SetColumnHeaderCoordinates(val columnId: Long, val coordinates: Coordinates) :
        BoardIntent

    data class SetColumnListCoordinates(val columnId: Long, val coordinates: Coordinates) :
        BoardIntent

    data class SetColumnCoordinates(val columnId: Long, val coordinates: Coordinates) : BoardIntent
    data class SetCardCoordinates(
        val cardId: Long,
        val columnId: Long,
        val coordinates: Coordinates
    ) : BoardIntent

}