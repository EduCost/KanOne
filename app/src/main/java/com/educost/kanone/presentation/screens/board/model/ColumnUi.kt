package com.educost.kanone.presentation.screens.board.model

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.geometry.Offset

data class ColumnUi(
    val id: Long,
    val name: String,
    val position: Int,
    val color: Int?,
    val cards: List<CardUi>,
    val coordinates: Coordinates = Coordinates(),
    val listCoordinates: Coordinates = Coordinates(),
    val headerCoordinates: Coordinates = Coordinates(),
    val listState: LazyListState = LazyListState()
) {

    fun getNewHeaderCenteredOffset(newOffset: Offset): Offset {
        val newX = newOffset.x - coordinates.width / 2
        val newY = newOffset.y - headerCoordinates.height / 2
        return Offset(newX, newY)
    }

    fun getNewCenterX(newOffset: Offset): Float = newOffset.x + coordinates.width / 2

    fun getCenterX(): Float = coordinates.position.x + coordinates.width / 2

}
