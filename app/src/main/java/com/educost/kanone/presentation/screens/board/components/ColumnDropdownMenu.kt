package com.educost.kanone.presentation.screens.board.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.model.ColumnUi

private enum class MenuState {
    MAIN,
    ORDER_BY,
}

@Composable
fun ColumnDropdownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    column: ColumnUi,
    onIntent: (BoardIntent) -> Unit,
) {
    var menuState by remember { mutableStateOf(MenuState.MAIN) }
    val rotation by animateFloatAsState(
        targetValue = if (menuState == MenuState.MAIN) 360f else 180f
    )
    DropdownMenu(
        modifier = modifier
            .animateContentSize(),
        expanded = expanded,
        onDismissRequest = {
            onIntent(BoardIntent.CloseColumnDropdownMenu)
            menuState = MenuState.MAIN
        },
        shape = RoundedCornerShape(12.dp)
    ) {

        AnimatedVisibility(visible = (menuState == MenuState.MAIN)) { // Create new card
            DropdownMenuItem(
                text = { Text(stringResource(R.string.board_dropdown_menu_add_card)) },
                onClick = {
                    onIntent(BoardIntent.StartCreatingCard(column.id, false))
                    onIntent(BoardIntent.CloseColumnDropdownMenu)
                },
                leadingIcon = {
                    Icon(Icons.Default.Add, null)
                }
            )
        }
        AnimatedVisibility(visible = (menuState == MenuState.MAIN)) { // Rename column
            DropdownMenuItem(
                text = { Text(stringResource(R.string.board_dropdown_menu_rename_column)) },
                onClick = {
                    onIntent(BoardIntent.OnRenameColumnClicked(column.id))
                    onIntent(BoardIntent.CloseColumnDropdownMenu)
                },
                leadingIcon = {
                    Icon(Icons.Default.Create, null)
                }
            )
        }

        // ========================= ORDER BY =========================
        AnimatedVisibility(visible = (menuState == MenuState.MAIN || menuState == MenuState.ORDER_BY)) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.board_dropdown_menu_order_by)) },
                onClick = {
                    menuState =
                        if (menuState == MenuState.MAIN) MenuState.ORDER_BY else MenuState.MAIN
                },
                leadingIcon = {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation)
                    )
                },
            )
        }
        AnimatedVisibility(visible = (menuState == MenuState.ORDER_BY)) {
            DropdownMenuItem(
                text = { Text("Name") },
                onClick = { }
            )
        }
        AnimatedVisibility(visible = (menuState == MenuState.ORDER_BY)) {
            DropdownMenuItem(
                text = { Text("Expiration Date") },
                onClick = { }
            )
        }
        AnimatedVisibility(visible = (menuState == MenuState.ORDER_BY)) {
            DropdownMenuItem(
                text = { Text("Label") },
                onClick = { }
            )
        }
        AnimatedVisibility(visible = (menuState == MenuState.ORDER_BY)) {
            DropdownMenuItem(
                text = { Text("Date Created") },
                onClick = { }
            )
        }
    }
}
