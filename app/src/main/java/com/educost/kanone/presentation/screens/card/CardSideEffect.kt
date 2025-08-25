package com.educost.kanone.presentation.screens.card

import com.educost.kanone.presentation.util.SnackbarEvent

sealed interface CardSideEffect {

    data class ShowSnackBar(val snackbarEvent: SnackbarEvent) : CardSideEffect

    data object OnNavigateBack : CardSideEffect

}