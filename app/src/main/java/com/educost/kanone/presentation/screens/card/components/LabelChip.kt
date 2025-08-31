package com.educost.kanone.presentation.screens.card.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
) {
    label.color?.let { colorInt ->

        val color = Color(colorInt)
        val selectedLabelColor = if (color.luminance() > 0.5f) Color.Black else Color.White

        FilterChip(
            modifier = modifier,
            selected = true,
            onClick = {},
            border = BorderStroke(
                width = 1.dp,
                color = color
            ),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = color,
                selectedLabelColor = selectedLabelColor,
                selectedTrailingIconColor = selectedLabelColor
            ),
            label = {
                Text(text = label.name)
            }
        )

    } ?: FilterChip(
        modifier = modifier,
        selected = true,
        onClick = {},
        label = {
            Text(text = label.name)
        },
    )
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
                )
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
                )
            )
        }
    }
}