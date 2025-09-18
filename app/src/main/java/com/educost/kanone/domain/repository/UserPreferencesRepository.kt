package com.educost.kanone.domain.repository

import com.educost.kanone.presentation.theme.ThemeData
import com.educost.kanone.presentation.theme.ThemeType
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    val selectedThemeType: Flow<ThemeData>

    suspend fun setThemeType(themeType: ThemeType)

}