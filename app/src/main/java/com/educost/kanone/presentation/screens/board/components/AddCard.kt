package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.model.ColumnUi
import com.educost.kanone.presentation.screens.board.state.BoardState

@Composable
fun AddCard(
    modifier: Modifier = Modifier,
    state: BoardState,
    onIntent: (BoardIntent) -> Unit,
    column: ColumnUi
) {

    val isAddingCard by remember(state.cardCreationState) {
        mutableStateOf(
            state.cardCreationState.columnId == column.id && state.cardCreationState.isAppendingToEnd
        )
    }

    val focusManager = LocalFocusManager.current
    LaunchedEffect(isAddingCard) {
        if (isAddingCard) {
            focusManager.clearFocus()
            focusManager.moveFocus(FocusDirection.Down)
        }
    }

    if (isAddingCard) {
        AddCardTextField(
            modifier = modifier.fillMaxWidth(),
            newCardTitle = state.cardCreationState.title ?: "",
            onTitleChange = { onIntent(BoardIntent.OnCardTitleChange(it)) },
            onConfirmCreateCard = { onIntent(BoardIntent.ConfirmCardCreation) }
        )
    } else {
        Box(
            modifier = modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AddCardButton { onIntent(BoardIntent.StartCreatingCard(column.id, true)) }
        }
    }
}

@Composable
fun AddCardButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.board_column_button_add_card),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun AddCardTextField(
    modifier: Modifier = Modifier,
    newCardTitle: String,
    onTitleChange: (String) -> Unit,
    onConfirmCreateCard: () -> Unit,
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(7.dp))
            .padding(12.dp)
    ) {
        BasicTextField(
            value = newCardTitle,
            onValueChange = { onTitleChange(it) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    onConfirmCreateCard()
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}