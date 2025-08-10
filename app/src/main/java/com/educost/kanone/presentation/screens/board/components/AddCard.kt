package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
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