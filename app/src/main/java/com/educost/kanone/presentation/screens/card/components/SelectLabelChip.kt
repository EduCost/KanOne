package com.educost.kanone.presentation.screens.card.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.educost.kanone.domain.model.Label
import com.educost.kanone.presentation.theme.KanOneTheme

@Composable
fun SelectLabelChip(
    modifier: Modifier = Modifier,
    label: Label,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    label.color?.let { colorInt ->

        val color by remember { mutableStateOf(Color(colorInt)) }
        val selectedLabelColor by remember {
            mutableStateOf(if (color.luminance() > 0.5f) Color.Black else Color.White)
        }

        FilterChip(
            modifier = modifier,
            selected = isSelected,
            onClick = onClick,
            trailingIcon = {
                if (isSelected) {
                    Icon(Icons.Default.Check, null)
                }
            },
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
                Text(
                    text = label.name,
                    maxLines = 1
                )
            }
        )

    } ?: FilterChip(
        modifier = modifier,
        selected = isSelected,
        onClick = onClick,

        label = {
            Text(text = label.name)
        },
        trailingIcon = {
            if (isSelected) {
                Icon(Icons.Default.Check, null)
            }
        },
    )
}

@PreviewLightDark
@Composable
private fun LabelChipPreview() {
    KanOneTheme {
        Surface {
            SelectLabelChip(
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
            SelectLabelChip(
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

@PreviewLightDark
@Composable
private fun LabelChipSelectedPreview() {
    KanOneTheme {
        Surface {
            SelectLabelChip(
                modifier = Modifier.padding(16.dp),
                label = Label(
                    id = 0,
                    name = "Label",
                    color = null
                ),
                isSelected = true
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun LabelChipColoredSelectedPreview() {
    KanOneTheme {
        Surface {
            SelectLabelChip(
                modifier = Modifier.padding(16.dp),
                label = Label(
                    id = 0,
                    name = "Label",
                    color = -25787
                ),
                isSelected = true
            )
        }
    }
}