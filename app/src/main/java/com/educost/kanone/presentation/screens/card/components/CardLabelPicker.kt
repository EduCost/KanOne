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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.domain.model.Label
import com.educost.kanone.presentation.theme.KanOneTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardLabelPicker(
    modifier: Modifier = Modifier,
    labels: List<Label>,
    selectedLabels: List<Label>,
    expanded: Boolean,
    onDismiss: () -> Unit,
    onCreateLabel: () -> Unit,
    onLabelSelected: (Label) -> Unit
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
                        Text(text = stringResource(R.string.card_dropdown_menu_label_title))
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
                            onClick = { onLabelSelected(label) },
                            isSelected = selectedLabels.contains(label)
                        )
                    },
                    enabled = false,
                    onClick = {}
                )
            }

            HorizontalDivider(Modifier.padding(top = 8.dp))

        }

        DropdownMenuItem(
            text = {
                Text(text = stringResource(R.string.card_dropdown_menu_add_label))
            },
            onClick = onCreateLabel,
            leadingIcon = {
                Icon(Icons.Outlined.NewLabel, contentDescription = null)
            }
        )

    }
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
                    expanded = true,
                    onCreateLabel = {},
                    onLabelSelected = {}
                )
            }
        }
    }
}