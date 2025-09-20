package com.educost.kanone.domain.logs

interface LogHandler {

    fun captureUncaughtExceptions()

    fun log(throwable: Throwable, from: String, level: LogLevel)

    fun getLogs(): List<LogEvent>

    fun clearLogs()

}