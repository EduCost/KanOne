package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.state.BoardState
import com.educost.kanone.presentation.theme.KanOneTheme

@Composable
fun AddColumn(
    modifier: Modifier = Modifier,
    state: BoardState,
    onIntent: (BoardIntent) -> Unit,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {

    var height by remember { mutableStateOf(56) }
    var width by remember { mutableStateOf(200) }

    if (state.topBarType == BoardAppBarType.ADD_COLUMN) {

        OutlinedTextField(
            modifier = modifier
                .height(height.dp)
                .width(width.dp),
            value = state.creatingColumnName ?: "",
            onValueChange = { onIntent(BoardIntent.OnColumnNameChanged(it)) },
            label = { Text(text = stringResource(R.string.board_textfield_label_column_name)) },
        )

    } else {

        Row(
            modifier = modifier
                .height(height.dp)
                .width(width.dp)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = MaterialTheme.shapes.small
                )
                .clickable(onClick = { onIntent(BoardIntent.StartCreatingColumn) })
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = contentColor
            )
            Spacer(Modifier.padding(8.dp))
            Text(
                text = stringResource(R.string.board_button_add_column),
                color = contentColor
            )
        }

    }
}

@PreviewLightDark
@Composable
private fun AddColumnButtonPreview() {
    KanOneTheme() {
        Surface {
            AddColumn(
                modifier = Modifier.padding(16.dp),
                state = BoardState(),
                onIntent = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AddColumnTextFieldPreview() {
    KanOneTheme() {
        Surface {
            AddColumn(
                modifier = Modifier.padding(16.dp),
                state = BoardState(topBarType = BoardAppBarType.ADD_COLUMN),
                onIntent = {}
            )
        }
    }
}