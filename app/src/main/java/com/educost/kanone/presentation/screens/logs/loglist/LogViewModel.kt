package com.educost.kanone.presentation.screens.logs.loglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.domain.logs.LogEvent
import com.educost.kanone.domain.logs.LogHandler
import com.educost.kanone.domain.util.JsonConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LogViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val jsonConverter: JsonConverter,
    private val logHandler: LogHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffectChannel = Channel<LogSideEffect>(Channel.BUFFERED)
    val sideEffectFlow = _sideEffectChannel.receiveAsFlow()

    init {
        viewModelScope.launch(dispatchers.io) {
            val logs = logHandler.getLogs().reversed()
            _uiState.value = _uiState.value.copy(logs = logs)
        }
    }

    fun onIntent(intent: LogIntent) {
        when (intent) {
            is LogIntent.OnNavigateBack -> onNavigateBack()
            is LogIntent.OnNavigateToLogDetail -> onNavigateToLogDetail(intent.log)
            is LogIntent.DeleteAllLogs -> deleteAllLogs()
        }
    }

    private fun onNavigateBack() {
        viewModelScope.launch(dispatchers.main) {
            _sideEffectChannel.send(LogSideEffect.OnNavigateBack)
        }
    }

    private fun onNavigateToLogDetail(log: LogEvent) {
        viewModelScope.launch(dispatchers.main) {
            val logJson = jsonConverter.toJson(log)
            _sideEffectChannel.send(LogSideEffect.OnNavigateToLogDetail(logJson))
        }
    }

    private fun deleteAllLogs() {
        viewModelScope.launch(dispatchers.io) {
            logHandler.clearLogs()
            _uiState.update { it.copy(logs = emptyList()) }
        }
    }
}