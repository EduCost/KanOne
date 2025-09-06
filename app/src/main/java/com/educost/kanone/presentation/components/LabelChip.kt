package com.educost.kanone.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.educost.kanone.domain.model.Label
import com.educost.kanone.presentation.theme.KanOneTheme

@Composable
fun LabelChip(
    modifier: Modifier = Modifier,
    label: Label,
    smallVersion: Boolean = false,
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
            smallVersion = smallVersion,
            isClickable = isClickable,
            onClick = onClick
        )

    } ?: LabelChip(
        modifier = modifier,
        text = label.name,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        onContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
        smallVersion = smallVersion,
        isClickable = isClickable,
        onClick = onClick
    )

}

@Composable
private fun LabelChip(
    modifier: Modifier = Modifier,
    text: String,
    containerColor: Color,
    onContainerColor: Color,
    smallVersion: Boolean,
    isClickable: Boolean,
    onClick: () -> Unit
) {

    val (height, padding) = remember {
        if (smallVersion) {
            28.dp to 12.dp
        } else {
            32.dp to 16.dp
        }
    }

    val textStyle = if (smallVersion) {
        MaterialTheme.typography.labelMedium
    } else {
        MaterialTheme.typography.labelLarge
    }


    Box(
        modifier = modifier
            .height(height)
            .clip(MaterialTheme.shapes.small)
            .background(containerColor)
            .clickable(
                enabled = isClickable,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = padding),
            text = text,
            style = textStyle,
            color = onContainerColor
        )
    }


}

/*@Composable
fun LabelChip(
    modifier: Modifier = Modifier,
    label: Label,
    isClickable: Boolean = true,
    onClick: () -> Unit = {}
) {
    label.color?.let { colorInt ->

        val color by remember(colorInt) { mutableStateOf(Color(colorInt)) }
        val selectedLabelColor by remember(colorInt) {
            mutableStateOf(if (color.luminance() > 0.5f) Color.Black else Color.White)
        }

        FilterChip(
            modifier = modifier,
            selected = true,
            enabled = isClickable,
            onClick = onClick,
            border = BorderStroke(
                width = 1.dp,
                color = color
            ),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = color,
                selectedLabelColor = selectedLabelColor,
                selectedTrailingIconColor = selectedLabelColor,
                disabledSelectedContainerColor = color,
                disabledLabelColor = selectedLabelColor,
                disabledTrailingIconColor = selectedLabelColor
            ),
            label = {
                Text(text = label.name)
            }
        )

    } ?: FilterChip(
        modifier = modifier,
        enabled = isClickable,
        selected = true,
        onClick = {},
        label = {
            Text(text = label.name)
        },
        colors = FilterChipDefaults.filterChipColors(
            disabledSelectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}*/

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
private fun SmallLabelChipPreview() {
    KanOneTheme {
        Surface {
            LabelChip(
                modifier = Modifier.padding(16.dp),
                label = Label(
                    id = 0,
                    name = "Label",
                    color = null
                ),
                smallVersion = true
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SmallLabelChipColoredPreview() {
    KanOneTheme {
        Surface {
            LabelChip(
                modifier = Modifier.padding(16.dp),
                label = Label(
                    id = 0,
                    name = "Label",
                    color = -25787
                ),
                smallVersion = true,
            )
        }
    }
}