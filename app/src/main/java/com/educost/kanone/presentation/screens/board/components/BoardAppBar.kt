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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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

        BoardAppBarType.ADD_COLUMN -> ActionTopBar(
            title = stringResource(R.string.board_appbar_create_column),
            leftIconContentDescription = stringResource(R.string.board_appbar_content_description_cancel_column_creation),
            onLeftIconClick = { onIntent(BoardIntent.CancelColumnCreation) },
            rightIconContentDescription = stringResource(R.string.board_appbar_content_description_confirm_column_creation),
            onRightIconClick = { onIntent(BoardIntent.ConfirmColumnCreation) }
        )

        BoardAppBarType.ADD_CARD -> ActionTopBar(
            title = stringResource(R.string.board_appbar_create_card),
            leftIconContentDescription = stringResource(R.string.board_appbar_content_description_cancel_card_creation),
            onLeftIconClick = { onIntent(BoardIntent.CancelCardCreation) },
            rightIconContentDescription = stringResource(R.string.board_appbar_content_description_confirm_card_creation),
            onRightIconClick = { onIntent(BoardIntent.ConfirmCardCreation) }
        )

        BoardAppBarType.RENAME_COLUMN -> ActionTopBar(
            title = stringResource(R.string.board_appbar_rename_column),
            leftIconContentDescription = stringResource(R.string.board_appbar_content_description_cancel_column_rename),
            onLeftIconClick = { onIntent(BoardIntent.CancelColumnRename) },
            rightIconContentDescription = stringResource(R.string.board_appbar_content_description_confirm_column_rename),
            onRightIconClick = { onIntent(BoardIntent.ConfirmColumnRename) }
        )
    }
}

@Composable
fun ActionTopBar(
    modifier: Modifier = Modifier,
    title: String,
    leftIcon: ImageVector = Icons.Filled.Clear,
    leftIconContentDescription: String,
    onLeftIconClick: () -> Unit,
    rightIcon: ImageVector = Icons.Filled.Check,
    rightIconContentDescription: String,
    onRightIconClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        title = {
            Text(
                text = title,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onLeftIconClick
            ) {
                Icon(
                    imageVector = leftIcon,
                    contentDescription = leftIconContentDescription
                )
            }
        },
        actions = {
            IconButton(
                onClick = onRightIconClick
            ) {
                Icon(
                    imageVector = rightIcon,
                    contentDescription = rightIconContentDescription
                )
            }
        },
    )
}
