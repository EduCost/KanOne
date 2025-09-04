package com.educost.kanone.presentation.screens.card.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.educost.kanone.R

@Composable
fun CardDeletionDialog(modifier: Modifier = Modifier, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.card_dialog_delete_card_title))
        },
        text = {
            Text(text = stringResource(R.string.card_dialog_delete_card_text))
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(text = stringResource(R.string.card_dialog_delete_card_confirm_button))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.card_dialog_delete_card_cancel_button))
            }
        }
    )
}