package com.educost.kanone.presentation.screens.board.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ThrottledDebouncedProcessor<K, V>(
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
    private val onProcess: (HashMap<K, V>) -> Unit,
    private val throttleTimeMs: Long = 16L,
    private val debounceTimeMs: Long = 200L
) {
    private val queuedUpdates = mutableMapOf<K, V>()
    private var debounceJob: Job? = null
    private var lastThrottleTime = 0L


    fun submit(key: K, value: V, isThrottling: Boolean) {
        queuedUpdates[key] = value

        if (isThrottling) {
            processWithThrottle()
        } else {
            processWithDebounce()
        }
    }

    private fun processWithThrottle() {
        val now = System.currentTimeMillis()
        if (now - lastThrottleTime < throttleTimeMs) return

        lastThrottleTime = now
        processNow()
    }

    private fun processWithDebounce() {
        debounceJob?.cancel()
        debounceJob = scope.launch(dispatcher) {
            delay(debounceTimeMs)
            processNow()
        }
    }

    private fun processNow() {
        if (queuedUpdates.isEmpty()) return

        val updatesToProcess = HashMap(queuedUpdates)
        queuedUpdates.clear()
        onProcess(updatesToProcess)
    }

    fun cancel() {
        debounceJob?.cancel()
    }
}