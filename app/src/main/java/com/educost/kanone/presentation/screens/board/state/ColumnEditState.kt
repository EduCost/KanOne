package com.educost.kanone.presentation.screens.board.state

data class ColumnEditState(
    val editingColumnId: Long? = null,
    val isRenaming: Boolean = false,
    val newColumnName: String? = null,
    val isShowingColorPicker: Boolean = false,
)