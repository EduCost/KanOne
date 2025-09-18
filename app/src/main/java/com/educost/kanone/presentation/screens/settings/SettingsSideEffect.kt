package com.educost.kanone.presentation.screens.settings

import com.educost.kanone.presentation.util.SnackbarEvent

sealed interface SettingsSideEffect {

    data object OnNavigateBack : SettingsSideEffect
    data class ShowSnackBar(val snackbarEvent: SnackbarEvent) : SettingsSideEffect


}