package com.educost.kanone.presentation.screens.logs.loglist

import com.educost.kanone.domain.logs.LogEvent

data class LogUiState(
    val logs: List<LogEvent> = emptyList(),
)
