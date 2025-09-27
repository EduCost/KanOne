package com.educost.kanone.presentation.screens.board

import com.educost.kanone.presentation.util.SnackbarEvent

sealed interface BoardSideEffect {

    data class NavigateToCardScreen(val cardId: Long) : BoardSideEffect
    data object OnNavigateBack : BoardSideEffect
    data object NavigateToSettings : BoardSideEffect
    data class ShowSnackBar(val snackbarEvent: SnackbarEvent) : BoardSideEffect
}