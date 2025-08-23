package com.educost.kanone.presentation.screens.card.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.educost.kanone.R

@Composable
fun CardDescription(
    modifier: Modifier = Modifier,
    cardDescription: String,
    newDescription: String? = null,
    onEditClick: () -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDescriptionSave: () -> Unit,
    isEditing: Boolean
) {

    val focusManager = LocalFocusManager.current

    LaunchedEffect(isEditing) {
        if (isEditing) focusManager.moveFocus(FocusDirection.Down)
        else focusManager.clearFocus()
    }

    val cardDescription = remember(newDescription, cardDescription) {
        if (isEditing) {
            newDescription ?: ""
        } else {
            cardDescription
        }
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        )
    ) {
        Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(text = stringResource(R.string.card_description))

                Spacer(Modifier.weight(1f))

                IconButton(
                    onClick = onEditClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.card_edit_description_button_content_description)
                    )
                }

            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = cardDescription,
                onValueChange = { onDescriptionChange(it) },
                placeholder = { Text(stringResource(R.string.card_description_text_field_placeholder)) },
                enabled = isEditing,
                minLines = 2,
                maxLines = 6,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onDescriptionSave() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledPlaceholderColor = OutlinedTextFieldDefaults.colors().unfocusedPlaceholderColor,
                    disabledTextColor = OutlinedTextFieldDefaults.colors().unfocusedTextColor,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                )
            )
        }
    }
}