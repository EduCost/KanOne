@file:OptIn(ExperimentalMaterial3Api::class)

package com.educost.kanone.presentation.screens.board.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.educost.kanone.R
import com.educost.kanone.presentation.components.ActionTopBar
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType

@Composable
fun BoardAppBar(
    modifier: Modifier = Modifier,
    boardName: String,
    type: BoardAppBarType = BoardAppBarType.DEFAULT,
    isDropdownMenuExpanded: Boolean,
    onIntent: (BoardIntent) -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    when (type) {
        BoardAppBarType.DEFAULT -> {
            TopAppBar(
                modifier = modifier,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                title = {
                    Text(
                        text = boardName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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
                },
                actions = {
                    IconButton(
                        onClick = { onIntent(BoardIntent.OpenBoardDropdownMenu) }
                    ) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.board_appbar_more_options_content_description)
                        )
                        BoardDropdownMenu(
                            isExpanded = isDropdownMenuExpanded,
                            onIntent = onIntent
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
            onRightIconClick = {
                keyboardController?.hide()
                onIntent(BoardIntent.ConfirmColumnCreation)
            }
        )

        BoardAppBarType.ADD_CARD -> ActionTopBar(
            title = stringResource(R.string.board_appbar_create_card),
            leftIconContentDescription = stringResource(R.string.board_appbar_content_description_cancel_card_creation),
            onLeftIconClick = { onIntent(BoardIntent.CancelCardCreation) },
            rightIconContentDescription = stringResource(R.string.board_appbar_content_description_confirm_card_creation),
            onRightIconClick = {
                keyboardController?.hide()
                onIntent(BoardIntent.ConfirmCardCreation)
            }
        )

        BoardAppBarType.RENAME_COLUMN -> ActionTopBar(
            title = stringResource(R.string.board_appbar_rename_column),
            leftIconContentDescription = stringResource(R.string.board_appbar_content_description_cancel_column_rename),
            onLeftIconClick = { onIntent(BoardIntent.CancelColumnRename) },
            rightIconContentDescription = stringResource(R.string.board_appbar_content_description_confirm_column_rename),
            onRightIconClick = {
                keyboardController?.hide()
                onIntent(BoardIntent.ConfirmColumnRename)
            }
        )
    }
}

@Composable
private fun BoardDropdownMenu(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onIntent: (BoardIntent) -> Unit
) {
    DropdownMenu(
        modifier = modifier,
        expanded = isExpanded,
        onDismissRequest = { onIntent(BoardIntent.CloseBoardDropdownMenu) },
    ) {
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.board_appbar_dropdown_menu_rename_board))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.ModeEdit,
                    contentDescription = null
                )
            },
            onClick = { onIntent(BoardIntent.OnRenameBoardClicked) }
        )
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.board_appbar_dropdown_menu_delete_board))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null
                )
            },
            colors = MenuDefaults.itemColors(
                leadingIconColor = Color.Red,
                textColor = Color.Red
            ),
            onClick = { onIntent(BoardIntent.OnDeleteBoardClicked) }
        )
    }
}
