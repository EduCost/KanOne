package com.educost.kanone.presentation.model

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.geometry.Offset
import com.educost.kanone.presentation.theme.Palette

data class ColumnUi(
    val id: Long,
    val name: String,
    val position: Int,
    val color: Palette,
    val boardId: Long,
    val bodyCoordinates: Coordinates = Coordinates(),
    val headerCoordinates: Coordinates = Coordinates(),
    val listState: LazyListState = LazyListState()
)
