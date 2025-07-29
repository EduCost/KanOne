@file:OptIn(ExperimentalMaterial3Api::class)

package com.educost.kanone.presentation.screens.board.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.educost.kanone.R
import com.educost.kanone.presentation.screens.board.BoardIntent

@Composable
fun BoardAppBar(
    modifier: Modifier = Modifier,
    boardName: String,
    type: BoardAppBarType = BoardAppBarType.DEFAULT,
    onIntent: (BoardIntent) -> Unit
) {
    when (type) {
        BoardAppBarType.DEFAULT -> {
            TopAppBar(
                modifier = modifier,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                title = {
                    Text(text = boardName)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { /*TODO*/ }
                    ) {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = stringResource(R.string.menu_icon_content_description)
                        )
                    }
                }
            )
        }

        BoardAppBarType.ADD_COLUMN -> {
            CenterAlignedTopAppBar(
                modifier = modifier,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                title = {
                    Text(
                        text = stringResource(R.string.board_appbar_create_column),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onIntent(BoardIntent.CancelColumnCreation) }
                    ) {
                        Icon(
                            Icons.Filled.Clear,
                            contentDescription = stringResource(R.string.board_appbar_content_description_cancel_column_creation)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onIntent(BoardIntent.ConfirmColumnCreation) }
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = stringResource(R.string.board_appbar_content_description_confirm_column_creation)
                        )
                    }
                },
            )
        }
    }
}
