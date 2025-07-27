@file:OptIn(ExperimentalMaterial3Api::class)

package com.educost.kanone.presentation.screens.board.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
                title = {
                    Text(text = boardName)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { /*TODO*/ }
                    ) {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = stringResource(R.string.menu_button)
                        )
                    }
                }
            )
        }
    }
}
