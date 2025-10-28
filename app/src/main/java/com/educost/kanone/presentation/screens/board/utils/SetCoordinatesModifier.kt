package com.educost.kanone.presentation.screens.board.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import com.educost.kanone.presentation.screens.board.model.Coordinates

sealed interface CoordinatesIntent {

    data class SetBoardCoordinates(val coordinates: Coordinates) : CoordinatesIntent

    data class SetColumnHeaderCoordinates(
        val columnId: Long,
        val coordinates: Coordinates
    ) : CoordinatesIntent

    data class SetColumnListCoordinates(
        val columnId: Long,
        val coordinates: Coordinates
    ) : CoordinatesIntent

    data class SetColumnCoordinates(
        val columnId: Long,
        val coordinates: Coordinates
    ) : CoordinatesIntent

    data class SetCardCoordinates(
        val cardId: Long,
        val coordinates: Coordinates
    ) : CoordinatesIntent
}

fun LayoutCoordinates.getCoordinates() = Coordinates(
    position = this.positionInRoot(),
    width = this.size.width,
    height = this.size.height
)


fun Modifier.setBoardCoordinates(onSetCoordinates: (CoordinatesIntent) -> Unit): Modifier {
    return this.onGloballyPositioned { layoutCoordinates ->
        onSetCoordinates(
            CoordinatesIntent.SetBoardCoordinates(
                coordinates = layoutCoordinates.getCoordinates()
            )
        )
    }
}

fun Modifier.setColumnHeaderCoordinates(columnId: Long, onSetCoordinates: (CoordinatesIntent) -> Unit): Modifier {
    return this.onGloballyPositioned { layoutCoordinates ->
        onSetCoordinates(
            CoordinatesIntent.SetColumnHeaderCoordinates(
                columnId = columnId,
                coordinates = layoutCoordinates.getCoordinates()
            )
        )
    }
}

fun Modifier.setColumnListCoordinates(columnId: Long, onSetCoordinates: (CoordinatesIntent) -> Unit): Modifier {
    return this.onGloballyPositioned { layoutCoordinates ->
        onSetCoordinates(
            CoordinatesIntent.SetColumnListCoordinates(
                columnId = columnId,
                coordinates = layoutCoordinates.getCoordinates()
            )
        )
    }
}

fun Modifier.setColumnCoordinates(columnId: Long, onSetCoordinates: (CoordinatesIntent) -> Unit): Modifier {
    return this.onGloballyPositioned { layoutCoordinates ->
        onSetCoordinates(
            CoordinatesIntent.SetColumnCoordinates(
                columnId = columnId,
                coordinates = layoutCoordinates.getCoordinates()
            )
        )
    }
}

fun Modifier.setCardCoordinates(cardId: Long, onSetCoordinates: (CoordinatesIntent) -> Unit): Modifier {
    return this.onGloballyPositioned { layoutCoordinates ->
        onSetCoordinates(
            CoordinatesIntent.SetCardCoordinates(
                cardId = cardId,
                coordinates = layoutCoordinates.getCoordinates()
            )
        )
    }
}