package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.model.BoardSizes
import com.educost.kanone.presentation.screens.board.state.BoardUiState
import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType
import com.educost.kanone.presentation.theme.KanOneTheme

@Composable
fun AddColumn(
    modifier: Modifier = Modifier,
    state: BoardUiState,
    onIntent: (BoardIntent) -> Unit,
    sizes: BoardSizes = BoardSizes()
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val isAddingNewColumn by remember(state.topBarType) {
        mutableStateOf(state.topBarType == BoardAppBarType.ADD_COLUMN)
    }

    LaunchedEffect(isAddingNewColumn) {
        if (isAddingNewColumn) {
            focusManager.clearFocus()
            focusManager.moveFocus(FocusDirection.Down)
        }
    }

    if (isAddingNewColumn) {

        Box(
            modifier = modifier
                .width(sizes.columnWidth)
                .clip(sizes.columnShape)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
        ) {
            Card(
                shape = sizes.columnHeaderShape,
                modifier = Modifier.padding(sizes.columnHeaderPadding),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ColumnHeaderCircle(sizes = sizes)

                    BasicTextField(
                        value = state.creatingColumnName ?: "",
                        onValueChange = { onIntent(BoardIntent.OnColumnNameChanged(it)) },
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
                                onIntent(BoardIntent.ConfirmColumnCreation)
                            }
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    } else {

        AddColumnButton(
            modifier = modifier.padding(sizes.addColumnButtonExternalPaddingValues),
            onClick = { onIntent(BoardIntent.StartCreatingColumn) },
            sizes = sizes
        )

    }
}


@Composable
private fun AddColumnButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    sizes: BoardSizes
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                brush = SolidColor(MaterialTheme.colorScheme.outline),
                shape = sizes.addColumnButtonShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.padding(sizes.addColumnButtonInternalPaddingValues),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.size(sizes.addColumnButtonIconSize),
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(sizes.addColumnButtonSpacer))
                Text(
                    text = stringResource(R.string.board_button_add_column),
                    fontSize = sizes.addColumnButtonTextSize,
                    lineHeight = sizes.addColumnButtonLineHeight
                )
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun AddColumnButtonPreview() {
    KanOneTheme {
        Surface {
            AddColumn(
                modifier = Modifier.padding(16.dp),
                state = BoardUiState(),
                onIntent = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AddColumnTextFieldPreview() {
    KanOneTheme {
        Surface {
            AddColumn(
                modifier = Modifier.padding(16.dp),
                state = BoardUiState(topBarType = BoardAppBarType.ADD_COLUMN),
                onIntent = {}
            )
        }
    }
}