package com.educost.kanone.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educost.kanone.dispatchers.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffectChannel = Channel<SettingsSideEffect>(Channel.BUFFERED)
    val sideEffectFlow = _sideEffectChannel.receiveAsFlow()


    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.OnNavigateBack -> onNavigateBack()
        }
    }

    private fun onNavigateBack() {
        viewModelScope.launch(dispatchers.main) {
            _sideEffectChannel.send(SettingsSideEffect.OnNavigateBack)
        }
    }

}