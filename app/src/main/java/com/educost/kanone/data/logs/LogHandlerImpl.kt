package com.educost.kanone.data.logs

import android.content.Context
import android.os.Build
import com.educost.kanone.BuildConfig
import com.educost.kanone.domain.logs.LogEvent
import com.educost.kanone.domain.logs.LogHandler
import com.educost.kanone.domain.logs.LogLevel
import com.educost.kanone.domain.logs.LogLocation
import com.educost.kanone.domain.util.JsonConverter
import java.io.File
import java.time.Instant

class LogHandlerImpl(
    private val jsonConverter: JsonConverter,
    private val context: Context
) : LogHandler {

    val logFolder = File(context.filesDir, "crash_logs")
    val logFile = File(logFolder, "crash_log.json")

    var defaultUncaughtExceptionHandler: Thread.UncaughtExceptionHandler? = null

    override fun captureUncaughtExceptions() {
        if (defaultUncaughtExceptionHandler != null) return
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            log(
                throwable = throwable,
                from = LogLocation.UNCAUGHT_EXCEPTION_HANDLER,
                level = LogLevel.ERROR
            )
            defaultUncaughtExceptionHandler?.uncaughtException(thread, throwable)
        }
    }

    override fun log(
        throwable: Throwable,
        from: String,
        level: LogLevel
    ) {

        if (!logFolder.exists()) logFolder.mkdirs()

        val timestamp = Instant.now().toString()
        val message = throwable.message ?: "Unknown"
        val stackTrace = throwable.stackTraceToString()
        val deviceSdkInt = Build.VERSION.SDK_INT
        val appVersionName = BuildConfig.VERSION_NAME

        val logEvent = LogEvent(
            timestamp = timestamp,
            message = message,
            stackTrace = stackTrace,
            level = level,
            from = from,
            deviceSdkInt = deviceSdkInt,
            appVersionName = appVersionName
        )

        val logEventJson = jsonConverter.toJson(logEvent)
        logFile.appendText(logEventJson + "\n")
    }

    override fun getLogs(): List<LogEvent> {
        if (!logFile.exists()) return emptyList()

        return try {
            logFile.readLines().map { logEventJson ->
                jsonConverter.fromJson(logEventJson, LogEvent::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun clearLogs() {
        logFile.delete()
    }
}