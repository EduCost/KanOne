package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.educost.kanone.presentation.screens.board.model.BoardSizes
import com.educost.kanone.presentation.screens.board.model.ColumnUi
import com.educost.kanone.presentation.screens.board.state.BoardUiState

@Composable
fun AddCard(
    modifier: Modifier = Modifier,
    state: BoardUiState,
    onIntent: (BoardIntent) -> Unit,
    column: ColumnUi,
    sizes: BoardSizes
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
            onConfirmCreateCard = { onIntent(BoardIntent.ConfirmCardCreation) },
            sizes = sizes
        )
    } else {
        Box(
            modifier = modifier
                .padding(sizes.addCardButtonSpacingTop)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AddCardButton(
                sizes = sizes,
                onClick = {
                    onIntent(
                        BoardIntent.StartCreatingCard(
                            columnId = column.id,
                            isAppendingToEnd = true
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun AddCardButton(modifier: Modifier = Modifier, onClick: () -> Unit, sizes: BoardSizes) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() },
    ) {
        Box(
            modifier = Modifier.padding(sizes.addCardButtonPaddingValues)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(sizes.addCardButtonIconSize),
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(sizes.addCardButtonSpacer))
                Text(
                    text = stringResource(R.string.board_column_button_add_card),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = sizes.addCardButtonFontSize,
                        lineHeight = sizes.addCardButtonLineHeight
                    )
                )
            }
        }
    }
}

@Composable
fun AddCardTextField(
    modifier: Modifier = Modifier,
    newCardTitle: String,
    onTitleChange: (String) -> Unit,
    onConfirmCreateCard: () -> Unit,
    sizes: BoardSizes
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier
            .clip(sizes.cardShape)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(7.dp))
            .padding(sizes.addCardTextFieldPaddingValues)
    ) {
        BasicTextField(
            value = newCardTitle,
            onValueChange = { onTitleChange(it) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = sizes.cardTitleFontSize,
                lineHeight = sizes.cardTitleLineHeight
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