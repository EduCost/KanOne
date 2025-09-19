package com.educost.kanone.presentation.screens.settings.logs

sealed interface LogIntent {
    data object OnNavigateBack : LogIntent
}