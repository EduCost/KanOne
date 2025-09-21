package com.educost.kanone.presentation.screens.home.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.educost.kanone.R

@Composable
fun CreateBoardDialog(
    modifier: Modifier = Modifier,
    onCreate: (String) -> Unit,
    onDismiss: () -> Unit
) {

    var boardName by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.home_dialog_title_create_board)) },
        text = {
            OutlinedTextField(
                value = boardName,
                onValueChange = { boardName = it },
                label = { Text(text = stringResource(R.string.home_dialog_textfield_label_board_name)) },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                enabled = boardName.isNotBlank(),
                onClick = { onCreate(boardName) }
            ) {
                Text(text = stringResource(R.string.home_dialog_button_create))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.home_dialog_button_cancel))
            }
        }
    )
}