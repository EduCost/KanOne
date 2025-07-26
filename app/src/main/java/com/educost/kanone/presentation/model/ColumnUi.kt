package com.educost.kanone.presentation.model

import androidx.compose.foundation.lazy.LazyListState
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.presentation.theme.Palette

data class ColumnUi(
    val id: Long,
    val name: String,
    val position: Int,
    val color: Palette,
    val cards: List<CardUi>,
    val bodyCoordinates: Coordinates = Coordinates(),
    val headerCoordinates: Coordinates = Coordinates(),
    val listState: LazyListState = LazyListState()
)
