package com.educost.kanone.presentation.screens.card.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.outlined.NewLabel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.domain.model.Label
import com.educost.kanone.presentation.components.ColorPickerDialog
import com.educost.kanone.presentation.theme.KanOneTheme

@Composable
fun CreateLabelDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: (Label) -> Unit,
) {

    var newName by rememberSaveable { mutableStateOf("") }
    var newColorInt by rememberSaveable { mutableStateOf<Int?>(null) }
    var isPickingColor by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Outlined.NewLabel, null)
        },
        title = {
            Text(stringResource(R.string.card_dialog_create_label_title))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text(stringResource(R.string.card_dialog_create_label_text_field_label)) },
                    singleLine = true,
                )

                Row(
                    Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    newColorInt?.let { colorInt ->

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(colorInt))
                                .clickable {
                                    isPickingColor = true
                                }
                        )

                        IconButton(
                            onClick = { newColorInt = null }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.card_dialog_create_label_button_remove_color_content_description)
                            )
                        }

                    } ?: run {

                        Button(
                            onClick = { isPickingColor = true }
                        ) {
                            Icon(
                                modifier = Modifier.size(ButtonDefaults.IconSize),
                                imageVector = Icons.Filled.ColorLens,
                                contentDescription = null
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(stringResource(R.string.card_dialog_create_label_button_pick_color))
                        }

                    }

                }
            }
        },
        confirmButton = {
            FilledTonalButton(
                enabled = newName.isNotBlank(),
                onClick = {
                    val newLabel = Label(
                        id = 0,
                        name = newName,
                        color = newColorInt
                    )
                    onConfirm(newLabel)
                }
            ) {
                Text(stringResource(R.string.card_dialog_create_label_button_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.card_dialog_create_label_button_cancel))
            }
        }

    )

    if (isPickingColor) {
        ColorPickerDialog(
            onDismiss = { isPickingColor = false },
            onConfirm = { newColorInt = it },
            initialColor = newColorInt
        )
    }
}

@Preview
@Composable
private fun CreateLabelDialogPreview() {
    KanOneTheme {
        Surface {
            CreateLabelDialog(onDismiss = {}, onConfirm = {})
        }
    }
}