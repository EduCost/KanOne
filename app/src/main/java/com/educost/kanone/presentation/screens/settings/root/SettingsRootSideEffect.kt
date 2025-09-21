package com.educost.kanone.presentation.screens.settings.root

import com.educost.kanone.presentation.util.SnackbarEvent

sealed interface SettingsRootSideEffect {
    data object OnNavigateBack : SettingsRootSideEffect
    data object OnNavigateToSettingsTheme : SettingsRootSideEffect
    data object OnNavigateToAbout : SettingsRootSideEffect
    data object OnNavigateToLog : SettingsRootSideEffect
    data class ShowSnackBar(val snackbarEvent: SnackbarEvent) : SettingsRootSideEffect
}