package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.educost.kanone.presentation.screens.board.model.BoardSizes
import com.educost.kanone.presentation.theme.KanOneTheme

@Composable
fun ResizableIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String? = null,
    sizes: BoardSizes
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .padding(sizes.resizableIconButtonPaddingValues)
        ) {
            Icon(
                modifier = Modifier.size(sizes.resizableIconButtonSize),
                imageVector = icon,
                contentDescription = contentDescription
            )
        }
    }
}

@Preview
@Composable
private fun ResizableIconButtonPreview() {
    KanOneTheme {
        Surface {
            ResizableIconButton(
                onClick = {},
                icon = Icons.Filled.MoreVert,
                contentDescription = null,
                sizes = BoardSizes()
            )
        }
    }
}