package com.educost.kanone.presentation.util

import androidx.compose.material3.SnackbarDuration

data class SnackbarEvent(
    val message: UiText,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val withDismissAction: Boolean = false,
    val action: SnackbarAction? = null
)

data class SnackbarAction(
    val label: UiText,
    val action: () -> Unit
)
