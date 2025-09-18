package com.educost.kanone.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.educost.kanone.domain.repository.UserPreferencesRepository
import com.educost.kanone.presentation.theme.ThemeData
import com.educost.kanone.presentation.theme.ThemeType
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_pref")

class UserPreferencesRepositoryImpl(private val context: Context) : UserPreferencesRepository {

    companion object {
        val SELECTED_THEME_TYPE = stringPreferencesKey("selected_theme_type")
        val MATERIAL_YOU_ENABLED = booleanPreferencesKey("material_you_enabled")
    }

    override val themePreference = context.dataStore.data.map { preferences ->
        val themeType = preferences[SELECTED_THEME_TYPE] ?: ThemeType.SYSTEM.name
        val isMaterialYouEnabled = preferences[MATERIAL_YOU_ENABLED] ?: true

        ThemeData(
            themeType = ThemeType.valueOf(themeType),
            isMaterialYouEnabled = isMaterialYouEnabled
        )
    }

    override suspend fun setThemeType(themeType: ThemeType) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_THEME_TYPE] = themeType.name
        }
    }

    override suspend fun setMaterialYouEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MATERIAL_YOU_ENABLED] = enabled
        }

    }
}