package com.educost.kanone.presentation.screens.board.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BoardSizes(
    val zoomPercentage: Float = 100f
) {
    private val zoom = zoomPercentage / 100f


    /*  ========= Column =========  */

    val columnWidth    = 300f.toDp(zoom)
    val columnsSpaceBy = 8f.toDp(zoom)
    val columnShape    = RoundedCornerShape(12f.toDp(zoom))

    val columnPaddingValues = PaddingValues(
        all = 16f.toClampedDp(zoom = zoom, min = 8f, max = 20f)
    )

    val columnFullScreenPaddingValues = PaddingValues(
        start = 16f.toClampedDp(zoom = zoom, min = 12f, max = 20f),
        top = 4f.dp,
        end = 16f.toClampedDp(zoom = zoom, min = 12f, max = 20f),
        bottom = 8f.dp,
    )

    /*  ========= Column =========  */


    /*  ========= Column Header =========  */

    val columnHeaderPadding       = 16f.toDp(zoom)
    val columnHeaderShape         = RoundedCornerShape(12f.toDp(zoom))
    val columnHeaderCircleSize    = 24f.toDp(zoom)
    val columnHeaderCirclePadding = 16f.toDp(zoom)
    val columnHeaderFontSize      = 18f.toSp(zoom)
    val columnHeaderLineHeight    = 24f.toSp(zoom)

    /*  ========= Column Header =========  */


    /*  ========= Column Card List =========  */

    val columnListPaddingValues = PaddingValues(
        horizontal = 8f.toDp(zoom),
    )
    val columnListSpaceBy = (8f * zoom).dp
    val columnListPadding = PaddingValues(
        bottom = 8f.toDp(zoom)
    )

    /*  ========= Column Card List =========  */


    /*  ========= Add Column Button =========  */

    val addColumnButtonExternalPaddingValues = PaddingValues(16f.toDp(zoom))
    val addColumnButtonInternalPaddingValues = PaddingValues(
        vertical = (8f * zoom).dp,
        horizontal = (24f * zoom).dp
    )

    val addColumnButtonShape    = RoundedCornerShape(8f.toDp(zoom))
    val addColumnButtonIconSize = 24f.toDp(zoom)
    val addColumnButtonSpacer   = 4f.toDp(zoom)

    val addColumnButtonTextSize   = 14f.toSp(zoom)
    val addColumnButtonLineHeight = 20f.toSp(zoom)

    /*  ========= Add Column Button =========  */


    /*  ========= Add Card Button =========  */

    val addCardButtonPaddingValues = PaddingValues(
        vertical = 8f.toDp(zoom),
        horizontal = 16f.toDp(zoom)
    )
    val addCardButtonSpacingTop = 8f.toDp(zoom)
    val addCardButtonSpacer     = 4f.toDp(zoom)
    val addCardButtonIconSize   = 24f.toDp(zoom)

    val addCardButtonFontSize   = 16f.toSp(zoom)
    val addCardButtonLineHeight = 24f.toSp(zoom)

    val addCardTextFieldPaddingValues = PaddingValues(12f.toDp(zoom))

    /*  ========= Add Card Button =========  */


    /*  ========= Card =========  */

    val cardShape = RoundedCornerShape((8f * zoom).dp)
    val cardPaddingValues = PaddingValues((12f * zoom).dp)

    // Title
    val cardTitleFontSize   = 16f.toSp(zoom)
    val cardTitleLineHeight = 24f.toSp(zoom)

    // Image
    val cardImageMaxHeight = (150f * zoom).dp
    val cardImageShape = RoundedCornerShape((8f * zoom).dp)

    // Tasks
    val cardTasksProgressIndicatorSize = 4f.toDp(zoom)
    val cardTasksProgressIndicatorSpacer = 8f.toDp(zoom)

    val cardTasksIconSize   = 12f.toDp(zoom)
    val cardTasksIconSpacer = 4f.toDp(zoom)

    val cardTasksFontSize   = 11f.toSp(zoom)
    val cardTasksLineHeight = 16f.toSp(zoom)

    // Due date
    val cardDueDatePaddingValues = PaddingValues(
        horizontal = 8f.toDp(zoom),
        vertical   = 4f.toDp(zoom)
    )
    val cardDueDateShape = RoundedCornerShape(8f.toDp(zoom))

    val cardDueDateFontSize   = 14f.toSp(zoom)
    val cardDueDateLineHeight = 20f.toSp(zoom)

    // Description
    val cardDescriptionIconSize = 20f.toDp(zoom)

    // Attachment
    val cardAttachmentIconSize = 18f.toDp(zoom)

    val cardAttachmentFontSize   = 14f.toSp(zoom)
    val cardAttachmentLineHeight = 20f.toSp(zoom)

    // Label
    val cardLabelsPaddingValues = PaddingValues(
        horizontal = 12f.toDp(zoom),
        vertical   = 6f.toDp(zoom)
    )
    val cardLabelsSpaceBy    = 8f.toDp(zoom)
    val cardLabelsPaddingTop = 12f.toDp(zoom)
    val cardLabelsShape      = RoundedCornerShape(8f.toDp(zoom))

    val cardLabelsFontSize   = 12f.toSp(zoom)
    val cardLabelsLineHeight = 16f.toSp(zoom)

    // Bottom row
    val cardHorizontalDividerPaddingValues = PaddingValues(
        top    = 8f.toDp(zoom),
        bottom = 12f.toDp(zoom)
    )

    val cardBottomRowPaddingTop = 8f.toDp(zoom)
    val cardBottomRowPaddingTopWithLabels = 12f.toDp(zoom)
    val cardBottomRowIconsSpacer = 8f.toDp(zoom)

    /*  ========= Card =========  */


    /*  ========= Resizable icon buttons =========  */

    val resizableIconButtonPaddingValues = PaddingValues(12f.toDp(zoom))
    val resizableIconButtonSize = 24f.toDp(zoom)

    /*  ========= Resizable icon buttons =========  */

}

private fun Float.toDp(zoom: Float): Dp = (this * zoom).dp

private fun Float.toSp(zoom: Float): TextUnit = (this * zoom).sp

private fun Float.toClampedDp(
    zoom: Float,
    min: Float = 8f,
    max: Float = 20f
): Dp {
    return (this * zoom).coerceIn(min, max).dp
}