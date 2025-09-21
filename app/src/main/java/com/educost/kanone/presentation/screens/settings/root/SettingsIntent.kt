package com.educost.kanone.presentation.screens.settings.root

sealed interface SettingsIntent {

    data object OnNavigateBack : SettingsIntent
    data object OnNavigateToSettingsTheme : SettingsIntent
    data object OnNavigateToAbout : SettingsIntent
    data object OnNavigateToLog : SettingsIntent

}