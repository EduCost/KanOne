package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.model.ColumnUi
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.screens.board.state.BoardState
import com.educost.kanone.presentation.theme.KanOneTheme

@Composable
fun ColumnHeader(
    modifier: Modifier = Modifier,
    column: ColumnUi,
    state: BoardState,
    onIntent: (BoardIntent) -> Unit,
) {

    val isDropdownMenuExpanded by remember(state.activeDropdownColumnId) {
        mutableStateOf(state.activeDropdownColumnId == column.id)
    }

    val isOnEditMode by remember(state.columnEditState.editingColumnId) {
        derivedStateOf {
            state.columnEditState.editingColumnId == column.id
        }
    }

    val headerColor by remember {
        derivedStateOf {
            if (column.color != null) {
                Color(column.color)
            } else {
                Color.Gray
            }
        }
    }

    val focusManager = LocalFocusManager.current
    LaunchedEffect(isOnEditMode) {
        if (isOnEditMode) {
            focusManager.clearFocus()
            focusManager.moveFocus(FocusDirection.Down)
        }
    }

    Card(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                onIntent(
                    BoardIntent.SetColumnHeaderCoordinates(
                        columnId = column.id,
                        coordinates = Coordinates(
                            position = layoutCoordinates.positionInRoot(),
                            width = layoutCoordinates.size.width,
                            height = layoutCoordinates.size.height
                        )
                    )
                )
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box( // Circle
                modifier = Modifier
                    .padding(16.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(headerColor)
            )

            if (isOnEditMode) {
                BasicTextField(
                    value = state.columnEditState.newColumnName ?: "",
                    onValueChange = { onIntent(BoardIntent.OnEditColumnNameChange(it)) },
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onIntent(BoardIntent.ConfirmColumnRename) }
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {

                Text(
                    modifier = Modifier.weight(1f),
                    text = column.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = { onIntent(BoardIntent.OpenColumnDropdownMenu(column.id)) }
                ) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = null
                    )
                }

                Box {
                    ColumnDropdownMenu(
                        expanded = isDropdownMenuExpanded,
                        column = column,
                        onIntent = onIntent
                    )
                }
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun ColumnHeaderPreview() {
    KanOneTheme {
        Surface {
            ColumnHeader(
                modifier = Modifier.padding(34.dp),
                column = ColumnUi(
                    id = 0,
                    name = "Column name",
                    position = 0,
                    color = null,
                    cards = emptyList()
                ),
                state = BoardState(),
                onIntent = {}
            )
        }
    }
}