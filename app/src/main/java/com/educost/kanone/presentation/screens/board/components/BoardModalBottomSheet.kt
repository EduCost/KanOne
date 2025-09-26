package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.components.SettingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardModalBottomSheet(
    modifier: Modifier = Modifier,
    board: BoardUi,
    onIntent: (BoardIntent) -> Unit
) {

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = { onIntent(BoardIntent.CloseBoardSettings) }
    ) {
        Card(
            modifier = Modifier.padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Zoom",
                    style = MaterialTheme.typography.titleMedium
                )
                ZoomSlider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    zoomPercentage = board.sizes.zoomPercentage,
                    onZoomChange = {
                        onIntent(BoardIntent.SetZoom(it))
                    }
                )
            }
        }

        Spacer(Modifier.height(8.dp))



        ShowCardImages(
            showImages = board.showImages,
            onToggle = { onIntent(BoardIntent.ToggleShowImages) }
        )

        SettingItem(
            title = "App settings",
            icon = Icons.Filled.Settings,
            onClick = { onIntent(BoardIntent.NavigateToSettings) },
            hasEndIcon = true
        )
    }
}


@Composable
private fun ShowCardImages(
    modifier: Modifier = Modifier,
    showImages: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = { onToggle() })
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Image,
            contentDescription = null
        )
        Spacer(Modifier.width(4.dp))
        Text(text = "Show card images")
        Spacer(Modifier.weight(1f))
        Switch(
            checked = showImages,
            onCheckedChange = { onToggle() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ZoomSlider(
    modifier: Modifier = Modifier,
    zoomPercentage: Float,
    onZoomChange: (Float) -> Unit
) {
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }

    Slider(
        modifier = modifier,
        value = zoomPercentage,
        onValueChange = { onZoomChange(it) },
        valueRange = 35f..120f,
        interactionSource = interactionSource,
        thumb = {
            Label(
                label = {
                    PlainTooltip(
                        modifier = Modifier,
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.inverseSurface,
                        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                            text = "${zoomPercentage.toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                },
                interactionSource = interactionSource,
            ) {
                SliderDefaults.Thumb(interactionSource = interactionSource)
            }
        },
    )
}