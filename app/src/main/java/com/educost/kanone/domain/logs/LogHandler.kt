package com.educost.kanone.domain.logs

interface LogHandler {

    fun captureUncaughtExceptions()

    fun log(throwable: Throwable, message: String?, from: String, level: LogLevel)

    fun getLogs(): List<LogEvent>

    fun clearLogs()

}