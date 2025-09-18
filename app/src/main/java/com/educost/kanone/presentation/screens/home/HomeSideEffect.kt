package com.educost.kanone.presentation.screens.home

import com.educost.kanone.presentation.util.SnackbarEvent

sealed interface HomeSideEffect {
    data class NavigateToBoardScreen(val boardId: Long) : HomeSideEffect
    data object OnNavigateToSettings : HomeSideEffect
    data class ShowSnackBar(val snackbarEvent: SnackbarEvent) : HomeSideEffect
}