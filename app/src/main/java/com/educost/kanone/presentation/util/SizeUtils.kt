package com.educost.kanone.presentation.util

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
fun isWindowWidthCompact(): Boolean {
    return currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass ==
            WindowWidthSizeClass.COMPACT
}