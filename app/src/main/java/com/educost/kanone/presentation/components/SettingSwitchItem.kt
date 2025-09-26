package com.educost.kanone.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educost.kanone.presentation.theme.KanOneTheme

@Composable
fun SettingSwitchItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(76.dp)
            .clickable(onClick = onToggle)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

        }

        Switch(
            checked = checked,
            onCheckedChange = { onToggle() }
        )

    }
}

@PreviewLightDark
@Composable
private fun SettingSwitchItemPreview() {
    KanOneTheme {
        Surface {
            SettingSwitchItem(
                title = "Switch item",
                icon = Icons.Filled.Adb,
                checked = true,
                onToggle = {}
            )
        }
    }
}


@PreviewLightDark
@Composable
private fun SettingSwitchItemPreview2() {
    KanOneTheme {
        Surface {
            SettingSwitchItem(
                title = "Item with subtitle",
                subtitle = "Subtitle",
                icon = Icons.Filled.Key,
                checked = false,
                onToggle = {}
            )
        }
    }
}