package com.educost.kanone.presentation.model

import androidx.compose.ui.geometry.Offset

data class Coordinates(
    val height: Int = 0,
    val width: Int = 0,
    val position: Offset = Offset.Zero,
)