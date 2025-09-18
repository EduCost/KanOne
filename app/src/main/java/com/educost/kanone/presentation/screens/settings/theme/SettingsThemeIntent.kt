package com.educost.kanone.presentation.screens.settings.theme

import com.educost.kanone.presentation.theme.ThemeType

sealed interface SettingsThemeIntent {

    data object OnNavigateBack : SettingsThemeIntent
    data class SetDarkMode(val darkMode: Boolean) : SettingsThemeIntent
    data class SetTheme(val themeType: ThemeType) : SettingsThemeIntent

}