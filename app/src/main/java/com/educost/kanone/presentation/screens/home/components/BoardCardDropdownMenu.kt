package com.educost.kanone.presentation.screens.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.educost.kanone.R

@Composable
fun BoardCardDropdownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        DropdownMenuItem(
            text = {
                Text(text = stringResource(R.string.home_board_card_dropdown_menu_rename))
            },
            onClick = onRename,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null
                )
            }
        )
        DropdownMenuItem(
            text = {
                Text(text = stringResource(R.string.home_board_card_dropdown_menu_delete))
            },
            onClick = onDelete,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null
                )
            },
            colors = MenuDefaults.itemColors(
                textColor = Color.Red,
                leadingIconColor = Color.Red
            )
        )
    }
}