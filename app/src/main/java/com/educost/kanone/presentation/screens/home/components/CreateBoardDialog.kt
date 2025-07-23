package com.educost.kanone.presentation.screens.home.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.educost.kanone.R
import com.educost.kanone.domain.model.Board
import com.educost.kanone.presentation.screens.home.HomeIntent
import com.educost.kanone.presentation.screens.home.HomeUiState

@Composable
fun CreateBoardDialog(
    modifier: Modifier = Modifier,
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onIntent(HomeIntent.DismissCreateBoardDialog) },
        title = { Text(text = stringResource(R.string.create_board)) },
        text = {
            OutlinedTextField(
                value = state.newBoardName,
                onValueChange = { onIntent(HomeIntent.OnNewBoardNameChange(it)) },
                label = { Text(text = stringResource(R.string.board_name)) }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onIntent(
                        HomeIntent.CreateBoard(
                            Board(
                                id = 0,
                                name = state.newBoardName,
                                emptyList()
                            )
                        )
                    )
                },
                enabled = if (state.newBoardName.isEmpty()) false else true
            ) {
                Text(text = stringResource(R.string.create))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onIntent(HomeIntent.DismissCreateBoardDialog)
                }
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}