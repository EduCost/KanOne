package com.educost.kanone.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.educost.kanone.domain.model.Label
import com.educost.kanone.presentation.screens.board.model.BoardSizes
import com.educost.kanone.presentation.theme.KanOneTheme

@Composable
fun LabelChip(
    modifier: Modifier = Modifier,
    label: Label,
    isClickable: Boolean = false,
    onClick: () -> Unit = {}
) {
    label.color?.let { colorInt ->

        val containerColor by remember(colorInt) {
            mutableStateOf(Color(colorInt))
        }
        val onContainerColor by remember(colorInt) {
            mutableStateOf(if (containerColor.luminance() > 0.5f) Color.Black else Color.White)
        }

        LabelChip(
            modifier = modifier,
            text = label.name,
            containerColor = containerColor,
            onContainerColor = onContainerColor,
            isClickable = isClickable,
            onClick = onClick
        )

    } ?: LabelChip(
        modifier = modifier,
        text = label.name,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        onContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
        isClickable = isClickable,
        onClick = onClick
    )

}

@Composable
fun ResizableLabelChip(
    modifier: Modifier = Modifier,
    label: Label,
    isClickable: Boolean = false,
    onClick: () -> Unit = {},
    sizes: BoardSizes
) {
    label.color?.let { colorInt ->

        val containerColor by remember(colorInt) {
            mutableStateOf(Color(colorInt))
        }
        val onContainerColor by remember(colorInt) {
            mutableStateOf(if (containerColor.luminance() > 0.5f) Color.Black else Color.White)
        }

        LabelChip(
            modifier = modifier,
            text = label.name,
            containerColor = containerColor,
            onContainerColor = onContainerColor,
            isClickable = isClickable,
            onClick = onClick,
            textStyle = MaterialTheme.typography.labelMedium.copy(
                fontSize = sizes.cardLabelsFontSize,
                lineHeight = sizes.cardLabelsLineHeight
            ),
            padding = sizes.cardLabelsPaddingValues,
            shape = sizes.cardLabelsShape
        )

    } ?: LabelChip(
        modifier = modifier,
        text = label.name,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        onContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
        isClickable = isClickable,
        onClick = onClick,
        textStyle = MaterialTheme.typography.labelMedium.copy(
            fontSize = sizes.cardLabelsFontSize,
            lineHeight = sizes.cardLabelsLineHeight
        ),
        padding = sizes.cardLabelsPaddingValues,
        shape = sizes.cardLabelsShape
    )

}

@Composable
private fun LabelChip(
    modifier: Modifier = Modifier,
    text: String,
    containerColor: Color,
    onContainerColor: Color,
    isClickable: Boolean,
    onClick: () -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    padding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    shape: CornerBasedShape = MaterialTheme.shapes.small
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(containerColor)
            .then(
                if (isClickable) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(padding),
            text = text,
            style = textStyle,
            color = onContainerColor
        )
    }

}


@PreviewLightDark
@Composable
private fun LabelChipPreview() {
    KanOneTheme {
        Surface {
            LabelChip(
                modifier = Modifier.padding(16.dp),
                label = Label(
                    id = 0,
                    name = "Label",
                    color = null
                ),
                onClick = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun LabelChipColoredPreview() {
    KanOneTheme {
        Surface {
            LabelChip(
                modifier = Modifier.padding(16.dp),
                label = Label(
                    id = 0,
                    name = "Label",
                    color = -25787
                ),
                onClick = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ResizableLabelChipPreview() {
    KanOneTheme {
        Surface {
            ResizableLabelChip(
                modifier = Modifier.padding(16.dp),
                label = Label(
                    id = 0,
                    name = "Label",
                    color = null
                ),
                sizes = BoardSizes(50f)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ResizableLabelChipColoredPreview() {
    KanOneTheme {
        Surface {
            ResizableLabelChip(
                modifier = Modifier.padding(16.dp),
                label = Label(
                    id = 0,
                    name = "Label",
                    color = -25787
                ),
                sizes = BoardSizes(50f)
            )
        }
    }
}
