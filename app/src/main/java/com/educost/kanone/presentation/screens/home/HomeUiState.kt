package com.educost.kanone.presentation.screens.home

import com.educost.kanone.domain.model.Board
import com.educost.kanone.presentation.util.UiText

data class HomeUiState(
    val boards: List<Board> = emptyList(),
    val isLoading: Boolean = false,

    val boardBeingRenamed: Long? = null,
    val boardBeingDeleted: Long? = null,
)
