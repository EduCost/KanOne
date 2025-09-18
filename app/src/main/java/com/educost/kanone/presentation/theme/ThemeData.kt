package com.educost.kanone.presentation.theme

import com.educost.kanone.presentation.screens.settings.theme.SettingsThemeIntent

data class ThemeData(
    val themeType: ThemeType = ThemeType.SYSTEM,
    val isMaterialYouEnabled: Boolean = true,
)