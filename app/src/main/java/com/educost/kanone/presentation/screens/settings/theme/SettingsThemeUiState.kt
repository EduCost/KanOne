package com.educost.kanone.presentation.screens.settings.theme

import com.educost.kanone.presentation.theme.ThemeType

data class SettingsThemeUiState(
    val themeType: ThemeType = ThemeType.SYSTEM,
)
