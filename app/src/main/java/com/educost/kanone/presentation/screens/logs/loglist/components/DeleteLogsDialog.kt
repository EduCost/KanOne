package com.educost.kanone.presentation.screens.logs.loglist.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DeleteLogsDialog(modifier: Modifier = Modifier, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete Logs") },
        text = { Text(text = "Are you sure you want to delete all logs?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}