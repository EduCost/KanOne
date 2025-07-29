package com.educost.kanone.presentation.model

import androidx.compose.foundation.lazy.LazyListState

data class ColumnUi(
    val id: Long,
    val name: String,
    val position: Int,
    val color: Int?,
    val cards: List<CardUi>,
    val bodyCoordinates: Coordinates = Coordinates(),
    val headerCoordinates: Coordinates = Coordinates(),
    val listState: LazyListState = LazyListState()
)
