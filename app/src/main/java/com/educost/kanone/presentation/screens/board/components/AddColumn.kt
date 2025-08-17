package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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
) {
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
                .width(300.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
        ) {
            Card(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box( // Circle
                        modifier = Modifier
                            .padding(16.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )

                    BasicTextField(
                        value = state.creatingColumnName ?: "",
                        onValueChange = { onIntent(BoardIntent.OnColumnNameChanged(it)) },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onIntent(BoardIntent.ConfirmColumnCreation) }
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    } else {

        OutlinedButton(
            modifier = modifier.padding(16.dp),
            onClick = { onIntent(BoardIntent.StartCreatingColumn) },
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
            )
            Spacer(Modifier.padding(4.dp))
            Text(
                text = stringResource(R.string.board_button_add_column),
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