package com.educost.kanone.domain.logs

data class LogEvent(
    val timestamp: String,
    val exceptionName: String,
    val message: String?,
    val stackTrace: String,
    val level: LogLevel,
    val from: String,
    val deviceSdkInt: Int,
    val appVersionName: String,
)
