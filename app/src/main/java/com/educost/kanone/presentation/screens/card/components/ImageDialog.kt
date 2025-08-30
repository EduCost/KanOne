package com.educost.kanone.presentation.screens.card.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.educost.kanone.R
import com.educost.kanone.domain.model.Attachment

@Composable
fun ImageDialog(
    modifier: Modifier = Modifier,
    attachment: Attachment,
    onDismiss: () -> Unit,
    onDelete: (Attachment) -> Unit,
) {

    val context = LocalContext.current

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Filled.Image, contentDescription = null)
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium),
                    model = ImageRequest.Builder(context)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .data(attachment.fileName)
                        .build(),
                    contentDescription = null,
                    error = rememberVectorPainter(Icons.Filled.Error),
                    placeholder = rememberVectorPainter(Icons.Filled.Image)
                )
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = { onDelete(attachment) }
            ) {
                Text(text = stringResource(R.string.card_dialog_image_button_delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.card_dialog_image_button_close))
            }
        },
    )
}