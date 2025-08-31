package com.educost.kanone.presentation.screens.card.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.NewLabel
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.educost.kanone.domain.model.Label
import com.educost.kanone.presentation.theme.KanOneTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardLabelPicker(
    modifier: Modifier = Modifier,
    labels: List<Label>,
    selectedLabels: List<Label>,
    expanded: Boolean,
    onDismiss: () -> Unit
) {

    DropdownMenu(
        modifier = modifier.heightIn(max = 400.dp),
        expanded = expanded,
        onDismissRequest = onDismiss,
    ) {

        if (labels.isNotEmpty()) {

            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Labels")
                    }
                },
                leadingIcon = {
                    Icon(Icons.AutoMirrored.Outlined.Label, contentDescription = null)
                },
                onClick = {},
                enabled = false,
                colors = MenuDefaults.itemColors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface
                )

            )

            HorizontalDivider(Modifier.padding(bottom = 8.dp))

            labels.forEach { label ->
                DropdownMenuItem(
                    text = {
                        SelectLabelChip(
                            label = label,
                            isSelected = selectedLabels.contains(label)
                        )
                    },
                    onClick = { /*TODO*/ },
                )
            }

            HorizontalDivider(Modifier.padding(top = 8.dp))

        }

        DropdownMenuItem(
            text = {
                Text(text = "Create new label")
            },
            onClick = {},
            leadingIcon = {
                Icon(Icons.Outlined.NewLabel, contentDescription = null)
            }
        )


    }

    /*AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,

        title = {
            Text(text = "Add Label")
        },
        text = {
            Column {
                HorizontalDivider()

                LazyColumn {
                    items(boardLabels) { label ->

                        val color =
                            if (label.color != null) Color(label.color).copy(alpha = 0.2f) else null

                        var selected by remember { mutableStateOf(false) }

                        LabelChip(
                            modifier = Modifier.padding(start = 12.dp),
                            label = label,
                            isSelected = selected,
                            onClick = { selected = !selected }
                        )

                        FilterChip(
                            modifier = Modifier.padding(start = 12.dp),
                            selected = true,
                            onClick = {},
                            label = {
                                Text(text = label.name)
                            },
                            colors = if (color != null) {
                                FilterChipDefaults.filterChipColors(
                                    containerColor = color,
                                    disabledContainerColor = color,
                                    selectedContainerColor = color,
                                )
                            } else {
                                FilterChipDefaults.filterChipColors()
                            },
                            border = BorderStroke(
                                width = 1.dp,
                                color = color ?: MaterialTheme.colorScheme.outline
                            )
                        )
                    }

                    item {
                        TextButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NewLabel,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(text = "Create new label")
                        }
                    }
                }

                HorizontalDivider()
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {}
            ) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "Cancel")
            }
        }
    )*/
}

@Preview
@Composable
private fun AddLabelDialogPreview() {
    KanOneTheme {
        Box(Modifier.size(250.dp)) {
            Box {
                CardLabelPicker(
                    onDismiss = {},
                    labels = listOf(
                        Label(
                            id = 0,
                            name = "Label",
                            color = -25787
                        )
                    ),
                    selectedLabels = emptyList(),
                    expanded = true
                )
            }
        }
    }
}