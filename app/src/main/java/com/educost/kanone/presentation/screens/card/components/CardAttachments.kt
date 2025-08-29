package com.educost.kanone.presentation.screens.card.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.educost.kanone.R
import com.educost.kanone.domain.model.Attachment

@Composable
fun CardAttachments(
    modifier: Modifier = Modifier,
    attachments: List<Attachment>,
    onPickImage: (String) -> Unit
) {

    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) onPickImage(uri.toString())
        }
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                1.dp
            )
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(text = stringResource(R.string.card_attachments))

                Spacer(Modifier.weight(1f))

                IconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = stringResource(R.string.card_add_attachment_button_content_description)
                    )
                }

            }

            if (attachments.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(attachments) { attachment ->

                        AsyncImage(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(MaterialTheme.shapes.small),
                            contentScale = ContentScale.Crop,
                            model = ImageRequest.Builder(context)
                                .data(attachment.fileName)
                                .build(),
                            contentDescription = null,
                            placeholder = rememberVectorPainter(Icons.Default.Build),
                            error = rememberVectorPainter(Icons.Default.Clear)
                        )
                    }

                }
            }
        }
    }
}