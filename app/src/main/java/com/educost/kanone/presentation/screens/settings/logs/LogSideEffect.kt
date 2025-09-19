package com.educost.kanone.presentation.screens.settings.logs

import com.educost.kanone.presentation.util.SnackbarEvent

sealed interface LogSideEffect {

    data object OnNavigateBack : LogSideEffect
    data class ShowSnackBar(val snackbarEvent: SnackbarEvent) : LogSideEffect

}