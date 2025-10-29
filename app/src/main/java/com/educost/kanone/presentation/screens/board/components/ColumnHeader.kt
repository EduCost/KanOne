package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.model.BoardSizes
import com.educost.kanone.presentation.screens.board.model.ColumnUi
import com.educost.kanone.presentation.screens.board.state.BoardUiState
import com.educost.kanone.presentation.screens.board.utils.setColumnHeaderCoordinates
import com.educost.kanone.presentation.theme.KanOneTheme

@Composable
fun ColumnHeader(
    modifier: Modifier = Modifier,
    column: ColumnUi,
    state: BoardUiState,
    onIntent: (BoardIntent) -> Unit,
    sizes: BoardSizes = BoardSizes(),
    isOnVerticalLayout: Boolean = false
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    val isDropdownMenuExpanded by remember(state.activeDropdownColumnId) {
        mutableStateOf(state.activeDropdownColumnId == column.id)
    }

    val isOnEditMode = remember(
        state.columnEditState.editingColumnId,
        state.columnEditState.isRenaming
    ) {
        state.columnEditState.editingColumnId == column.id && state.columnEditState.isRenaming
    }


    val focusManager = LocalFocusManager.current
    LaunchedEffect(isOnEditMode) {
        if (isOnEditMode) {
            focusManager.clearFocus()
            focusManager.moveFocus(FocusDirection.Down)
        }
    }

    Card(
        shape = sizes.columnHeaderShape,
        modifier = modifier
            .setColumnHeaderCoordinates(
                columnId = column.id,
                onSetCoordinates = { onIntent(BoardIntent.OnSetCoordinates(it)) }
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = isOnVerticalLayout,
                    onClick = { onIntent(BoardIntent.ToggleExpandColumn(column.id)) }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColumnHeaderCircle(
                sizes = sizes,
                onClick = { onIntent(BoardIntent.StartEditingColumnColor(column.id)) },
                color = column.color
            )

            if (isOnEditMode) {
                BasicTextField(
                    value = state.columnEditState.newColumnName ?: "",
                    onValueChange = { onIntent(BoardIntent.OnEditColumnNameChange(it)) },
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = sizes.columnHeaderFontSize,
                        lineHeight = sizes.columnHeaderLineHeight
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            onIntent(BoardIntent.ConfirmColumnRename)
                        }
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
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = sizes.columnHeaderFontSize,
                        lineHeight = sizes.columnHeaderLineHeight
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                ResizableIconButton(
                    onClick = { onIntent(BoardIntent.OpenColumnDropdownMenu(column.id)) },
                    icon = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.board_column_header_more_options_content_description),
                    sizes = sizes
                )

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

@Composable
fun ColumnHeaderCircle(
    modifier: Modifier = Modifier,
    sizes: BoardSizes= BoardSizes(),
    onClick: () -> Unit = {},
    color: Int? = null
) {
    val headerColor = remember(color) {
        if (color != null) Color(color)
        else Color.Gray
    }

    Box(
        modifier = modifier
            .padding(sizes.columnHeaderCirclePadding)
            .size(sizes.columnHeaderCircleSize)
            .clip(CircleShape)
            .background(headerColor)
            .clickable { onClick() }
    )
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
                state = BoardUiState(),
                onIntent = {}
            )
        }
    }
}