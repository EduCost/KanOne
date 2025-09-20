package com.educost.kanone.presentation.screens.logs.loglist

import com.educost.kanone.presentation.util.SnackbarEvent

sealed interface LogSideEffect {

    data object OnNavigateBack : LogSideEffect
    data class OnNavigateToLogDetail(val logJson: String) : LogSideEffect
    data class ShowSnackBar(val snackbarEvent: SnackbarEvent) : LogSideEffect

}