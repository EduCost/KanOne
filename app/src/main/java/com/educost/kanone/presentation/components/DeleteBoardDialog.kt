package com.educost.kanone.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.educost.kanone.R

@Composable
fun DeleteBoardDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.dialog_board_delete_title))
        },
        text = {
            Text(text = stringResource(R.string.dialog_board_delete_text))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDelete()
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.dialog_board_delete_confirm_button))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.dialog_board_delete_cancel_button))
            }
        }
    )
}