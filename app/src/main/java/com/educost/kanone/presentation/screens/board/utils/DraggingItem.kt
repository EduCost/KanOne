package com.educost.kanone.presentation.screens.board.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import com.educost.kanone.presentation.screens.board.components.BoardColumn
import com.educost.kanone.presentation.screens.board.components.ColumnCard
import com.educost.kanone.presentation.screens.board.model.BoardSizes
import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.screens.board.model.CardUi
import com.educost.kanone.presentation.screens.board.model.ColumnUi
import com.educost.kanone.presentation.screens.board.state.BoardUiState


/**
 * Applies a "placeholder" visual effect to the original composable while a copy of it is being dragged.
 *
 * In a typical drag-and-drop scenario, the original item remains in its layout position
 * while a floating copy is moved by the user. This Modifier should be applied to the
 * **original item**.
 *
 * When [isDragging] is true, it renders the original item as a semi-transparent "ghost".
 * So it visually marks the spot where the dragged item will return if the drag is canceled.
 *
 * @param isDragging True if the corresponding item is currently being dragged, false otherwise.
 */
fun Modifier.dragPlaceholder(isDragging: Boolean): Modifier {
    return if (isDragging)
        this.graphicsLayer {
            colorFilter = ColorFilter.tint(Color.Gray)
            alpha = 0.05f
        }
    else this
}


/**
 * A composable that renders a visual representation of a [BoardColumn] being dragged.
 *
 * This is a copy of the original item that is being dragged, while the original item stays
 * as a semi-transparent "ghost", this is the actual item tha is being dragged and following
 * the finger/cursor of the user.
 */
@Composable
fun DraggingColumn(
    modifier: Modifier = Modifier,
    column: ColumnUi,
    itemOffset: Offset,
    board: BoardUi,
    isOnVerticalLayout: Boolean,
) {
    val localDensity = LocalDensity.current

    BoardColumn(
        modifier = modifier
            .graphicsLayer {
                translationY = itemOffset.y
                translationX = itemOffset.x
                rotationZ = 2f
            }
            .then(other = when {
                isOnVerticalLayout -> Modifier.fillMaxWidth()

                else -> Modifier
                    .width(with(localDensity) { column.coordinates.width.toDp() })
                    .height(with(localDensity) { column.coordinates.height.toDp() })
            }),
        column = column,
        state = BoardUiState(),
        onIntent = { },
        sizes =
            if (isOnVerticalLayout) BoardSizes()
            else board.sizes,
        isOnVerticalLayout = isOnVerticalLayout,
        showCardImages = board.showImages
    )
}


/**
 * A composable that renders a visual representation of a [ColumnCard] being dragged.
 *
 * This is a copy of the original item that is being dragged, while the original item stays
 * as a semi-transparent "ghost", this is the actual item tha is being dragged and following
 * the finger/cursor of the user.
 */
@Composable
fun DraggingCard(
    modifier: Modifier = Modifier,
    card: CardUi,
    itemOffset: Offset,
    board: BoardUi,
    isOnVerticalLayout: Boolean,
) {
    val localDensity = LocalDensity.current

    ColumnCard(
        modifier = modifier
            .graphicsLayer {
                translationY = itemOffset.y
                translationX = itemOffset.x
                rotationZ = 2f
            }
            .width(with(localDensity) { card.coordinates.width.toDp() })
            .height(with(localDensity) { card.coordinates.height.toDp() }),
        card = card,
        shouldShowImage = board.showImages,
        sizes =
            if (isOnVerticalLayout) BoardSizes()
            else board.sizes,
    )
}