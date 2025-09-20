package com.educost.kanone.presentation.screens.logs.loglist

import com.educost.kanone.domain.logs.LogEvent

sealed interface LogIntent {
    data object OnNavigateBack : LogIntent
    data class OnNavigateToLogDetail(val log: LogEvent) : LogIntent
    data object DeleteAllLogs : LogIntent

}