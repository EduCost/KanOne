package com.educost.kanone.presentation.screens.logs.logdetail

import androidx.lifecycle.ViewModel
import com.educost.kanone.domain.logs.LogEvent
import com.educost.kanone.domain.util.JsonConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class LogDetailViewModel @Inject constructor(
    private val jsonConverter: JsonConverter
) : ViewModel() {

    private val _logEvent = MutableStateFlow<LogEvent?>(null)
    val logEvent = _logEvent.asStateFlow()

    fun parseLogEvent(logEventJson: String) {
        _logEvent.value = jsonConverter.fromJson(logEventJson, LogEvent::class.java)
    }

}