package com.educost.kanone.presentation.screens.settings.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.domain.repository.UserPreferencesRepository
import com.educost.kanone.presentation.screens.settings.components.SettingsSideEffect
import com.educost.kanone.presentation.theme.ThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsThemeViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsThemeUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffectChannel = Channel<SettingsSideEffect>(Channel.BUFFERED)
    val sideEffectFlow = _sideEffectChannel.receiveAsFlow()

    init {
        viewModelScope.launch(dispatchers.main) {
            userPreferencesRepository.selectedThemeType.collect { themeData ->
                _uiState.update { it.copy(themeData = themeData) }
            }
        }
    }

    fun onIntent(intent: SettingsThemeIntent) {
        when (intent) {
            is SettingsThemeIntent.OnNavigateBack -> onNavigateBack()
            is SettingsThemeIntent.SetDarkMode -> setDarkMode(intent.darkMode)
            is SettingsThemeIntent.SetTheme -> setTheme(intent.themeType)

        }
    }


    private fun setDarkMode(darkMode: Boolean) {
        viewModelScope.launch(dispatchers.main) {
            if (darkMode) {
                userPreferencesRepository.setThemeType(ThemeType.DARK)
            } else {
                userPreferencesRepository.setThemeType(ThemeType.LIGHT)
            }
        }
    }

    private fun setTheme(themeType: ThemeType) {
        viewModelScope.launch(dispatchers.main) {
            userPreferencesRepository.setThemeType(themeType)
        }
    }

    private fun onNavigateBack() {
        viewModelScope.launch(dispatchers.main) {
            _sideEffectChannel.send(SettingsSideEffect.OnNavigateBack)
        }
    }


}