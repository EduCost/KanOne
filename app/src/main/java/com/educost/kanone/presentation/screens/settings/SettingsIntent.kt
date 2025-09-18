package com.educost.kanone.presentation.screens.settings

sealed interface SettingsIntent {

    data object OnNavigateBack : SettingsIntent

}