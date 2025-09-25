package com.educost.kanone.presentation.screens.board.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BoardSizes(
    val zoomPercentage: Float = 100f
) {
    private val zoom = zoomPercentage / 100f

    val columnWidth = (300f * zoom).dp
    val columnsSpaceBy = (8f * zoom).dp
    val columnShape = (12f * zoom).dp

    val columnPaddingValues = PaddingValues(
        start = (16 * zoom).dp,
        top = (16 * zoom).dp,
        end = (16 * zoom).dp,
        bottom = (16 * zoom).dp
    )

    val columnFullScreenPaddingValues = PaddingValues(
        start = (16f * zoom).dp,
        top = (4f * zoom).dp,
        end = (16f * zoom).dp,
        bottom = (8f * zoom).dp,
    )

    val columnHeaderPadding = (16f * zoom).dp
    val columnHeaderShape = RoundedCornerShape((12f * zoom).dp)
    val columnHeaderCircleSize = (24f * zoom).dp
    val columnHeaderCirclePadding = (16f * zoom).dp
    val columnHeaderFontSize = (18f * zoom).sp
    val columnHeaderLineHeight = (24f * zoom).sp

    val columnListPaddingValues = PaddingValues(
        start = (8f * zoom).dp,
        end = (8f * zoom).dp,
        bottom = (8f * zoom).dp
    )
    val columnListSpaceBy = (8f * zoom).dp

    val addColumnPadding = (16f * zoom).dp
    val addColumnTextSize = (14f * zoom).sp
    val addColumnLineHeight = (20f * zoom).sp


    // Card
    val addCardButtonSpacingTop = (8f * zoom).dp
    val addCardButtonPaddingValues = PaddingValues(
        vertical = (8f * zoom).dp,
        horizontal = (16f * zoom).dp
    )
    val addCardButtonIconSize = (24f * zoom).dp
    val addCardButtonSpacer = (4f * zoom).dp
    val addCardButtonFontSize = (16f * zoom).sp
    val addCardButtonLineHeight = (24f * zoom).sp

    val addCardTextFieldPaddingValues = PaddingValues((12f * zoom).dp)


    val cardTitleFontSize = (16f * zoom).sp
    val cardTitleLineHeight = (24f * zoom).sp

    val cardShape = RoundedCornerShape((8f * zoom).dp)
    val cardPaddingValues = PaddingValues((12f * zoom).dp)

    val cardImageMaxHeight = (150f * zoom).dp
    val cardImageShape = RoundedCornerShape((8f * zoom).dp)

    val cardTasksProgressIndicatorSize = (4f * zoom).dp
    val cardTasksProgressIndicatorSpacer = (8f * zoom).dp
    val cardTasksIconSize = (12f * zoom).dp
    val cardTasksIconSpacer = (4f * zoom).dp
    val cardTasksFontSize = (11f * zoom).sp
    val cardTasksLineHeight = (16f * zoom).sp

    val cardDueDatePaddingValues = PaddingValues(
        horizontal = (8f * zoom).dp,
        vertical = (4f * zoom).dp
    )
    val cardDueDateShape = RoundedCornerShape((8f * zoom).dp)
    val cardDueDateFontSize = (14f * zoom).sp
    val cardDueDateLineHeight = (20f * zoom).sp

    val cardDescriptionIconSize = (20f * zoom).dp
    val cardAttachmentIconSize = (18f * zoom).dp
    val cardAttachmentFontSize = (14f * zoom).sp
    val cardAttachmentLineHeight = (20f * zoom).sp

    val cardLabelsSpaceBy = (8f * zoom).dp
    val cardLabelsPaddingTop = (12f * zoom).dp

    val cardHorizontalDividerPaddingValues = PaddingValues(
        top = (8f * zoom).dp,
        bottom = (12f * zoom).dp
    )

    val cardBottomRowPaddingTop = (8f * zoom).dp
    val cardBottomRowPaddingTopWithLabels = (12f * zoom).dp
    val cardBottomRowIconsSpacer = (8f * zoom).dp


}