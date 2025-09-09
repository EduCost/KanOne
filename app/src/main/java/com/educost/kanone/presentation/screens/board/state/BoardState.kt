package com.educost.kanone.presentation.screens.board.state

import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType

data class BoardState(
    val board: BoardUi? = null,
    val isLoading: Boolean = false,
    val topBarType: BoardAppBarType = BoardAppBarType.DEFAULT,
    val activeDropdownColumnId: Long? = null,
    val creatingColumnName: String? = null,
    val columnEditState: ColumnEditState = ColumnEditState(),
    val cardCreationState: CardCreationState = CardCreationState(),
    val isBoardDropdownMenuExpanded: Boolean = false,
    val isRenamingBoard: Boolean = false,
    val dragState: DragState = DragState(),
)

data class CardCreationState(
    val isAppendingToEnd: Boolean = false,
    val title: String? = null,
    val columnId: Long? = null,
)

data class ColumnEditState(
    val editingColumnId: Long? = null,
    val isRenaming: Boolean = false,
    val newColumnName: String? = null,
    val isShowingColorPicker: Boolean = false,
)