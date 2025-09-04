package com.educost.kanone.presentation.screens.card.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest

@Composable
fun CardCover(
    modifier: Modifier = Modifier,
    cover: String,
    onRemoveCover: () -> Unit
) {

    val context = LocalContext.current
    var isShowingDropdownMenu by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
        contentAlignment = Alignment.Center
    ) {
        Box {
            DropdownMenu(
                expanded = isShowingDropdownMenu,
                onDismissRequest = { isShowingDropdownMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Remove cover") },
                    onClick = {
                        isShowingDropdownMenu = false
                        onRemoveCover()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Remove, null)
                    }
                )
            }
        }
        AsyncImage(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            isShowingDropdownMenu = true
                        }
                    )
                },
            contentScale = ContentScale.FillWidth,
            model = ImageRequest.Builder(context)
                .diskCachePolicy(CachePolicy.DISABLED)
                .data(cover)
                .build(),
            contentDescription = null,
            placeholder = rememberVectorPainter(Icons.Default.Image),
            error = rememberVectorPainter(Icons.Default.Clear)
        )
    }
}