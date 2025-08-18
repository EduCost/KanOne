package com.educost.kanone.presentation.screens.board.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HistoryToggleOff
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.educost.kanone.R
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.board.model.ColumnUi
import com.educost.kanone.presentation.screens.board.utils.CardOrder
import com.educost.kanone.presentation.screens.board.utils.OrderType

private enum class MenuType {
    DEFAULT,
    ORDER_BY,
}

@Composable
fun ColumnDropdownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    column: ColumnUi,
    onIntent: (BoardIntent) -> Unit,
) {
    var menuType by remember { mutableStateOf(MenuType.DEFAULT) }
    var orderByType by remember { mutableStateOf<CardOrder?>(null) }
    val rotation by animateFloatAsState(
        targetValue = if (menuType == MenuType.DEFAULT) 360f else 180f
    )
    val dismiss = remember {
        {
            onIntent(BoardIntent.CloseColumnDropdownMenu)
            menuType = MenuType.DEFAULT
            orderByType = null
        }
    }
    DropdownMenu(
        modifier = modifier
            .animateContentSize(),
        expanded = expanded,
        onDismissRequest = { dismiss() },
        shape = RoundedCornerShape(12.dp)
    ) {

        AnimatedVisibility(visible = (menuType == MenuType.DEFAULT)) { // Create new card
            DropdownMenuItem(
                text = { Text(stringResource(R.string.board_dropdown_menu_add_card)) },
                onClick = {
                    onIntent(BoardIntent.StartCreatingCard(column.id, false))
                    dismiss()
                },
                leadingIcon = {
                    Icon(Icons.Default.Add, null)
                }
            )
        }
        AnimatedVisibility(visible = (menuType == MenuType.DEFAULT)) { // Rename column
            DropdownMenuItem(
                text = { Text(stringResource(R.string.board_dropdown_menu_rename_column)) },
                onClick = {
                    onIntent(BoardIntent.OnRenameColumnClicked(column.id))
                    dismiss()
                },
                leadingIcon = {
                    Icon(Icons.Default.Create, null)
                }
            )
        }

        
        /*  ============================== ORDER BY ================================  */

        AnimatedVisibility(visible = (menuType == MenuType.DEFAULT || menuType == MenuType.ORDER_BY)) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.board_dropdown_menu_order_by)) },
                onClick = {
                    menuType = if (menuType == MenuType.DEFAULT)
                        MenuType.ORDER_BY
                    else
                        MenuType.DEFAULT
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

        AnimatedVisibility(visible = (menuType == MenuType.ORDER_BY)) {

            Column {

                OrderByDropdownMenuItem(
                    name = stringResource(R.string.board_dropdown_menu_order_by_name),
                    isSelected = orderByType == CardOrder.NAME,
                    onClick = { orderByType = CardOrder.NAME },
                    onConfirmOrder = { orderType ->
                        onIntent(
                            BoardIntent.OnOrderByClicked(
                                columnId = column.id,
                                orderType = orderType,
                                cardOrder = CardOrder.NAME
                            )
                        )
                        dismiss()
                    },
                    icon = Icons.Filled.Abc
                )

                OrderByDropdownMenuItem(
                    name = stringResource(R.string.board_dropdown_menu_order_by_due_date),
                    isSelected = orderByType == CardOrder.DUE_DATE,
                    onClick = { orderByType = CardOrder.DUE_DATE },
                    onConfirmOrder = { orderType ->
                        onIntent(
                            BoardIntent.OnOrderByClicked(
                                columnId = column.id,
                                orderType = orderType,
                                cardOrder = CardOrder.DUE_DATE
                            )
                        )
                        dismiss()
                    },
                    icon = Icons.Outlined.Event
                )

                OrderByDropdownMenuItem(
                    name = stringResource(R.string.board_dropdown_menu_order_by_date_created),
                    isSelected = orderByType == CardOrder.DATE_CREATED,
                    onClick = { orderByType = CardOrder.DATE_CREATED },
                    onConfirmOrder = { orderType ->
                        onIntent(
                            BoardIntent.OnOrderByClicked(
                                columnId = column.id,
                                orderType = orderType,
                                cardOrder = CardOrder.DATE_CREATED
                            )
                        )
                        dismiss()
                    },
                    icon = Icons.Filled.HistoryToggleOff
                )

            }

        }

        /*  =======================================================================  */

        AnimatedVisibility(visible = (menuType == MenuType.DEFAULT)) { // Delete column
            DropdownMenuItem(
                text = {
                    Text(
                        stringResource(R.string.board_dropdown_menu_delete_column),
                        color = Color.Red
                    )
                },
                onClick = {
                    onIntent(BoardIntent.OnDeleteColumnClicked(column.id))
                    dismiss()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.Red
                    )
                }
            )
        }
    }
}


@Composable
fun OrderByDropdownMenuItem(
    modifier: Modifier = Modifier,
    name: String,
    onConfirmOrder: (OrderType) -> Unit,
    onClick: () -> Unit,
    isSelected: Boolean,
    icon: ImageVector,
) {
    /*TODO: Improve the animations*/

    AnimatedVisibility(visible = !isSelected) {
        DropdownMenuItem(
            modifier = modifier,
            text = { Text(name) },
            onClick = onClick,
            leadingIcon = {
                Icon(icon, null)
            }
        )
    }

    AnimatedVisibility(visible = isSelected) {
        Row(
            modifier = modifier
                .padding(horizontal = 12.dp)
        ) {
            SelectOrderTypeCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ArrowDropUp,
                contentDescription = stringResource(R.string.board_dropdown_menu_order_by_ascending),
                onClick = { onConfirmOrder(OrderType.ASCENDING) }
            )

            Spacer(Modifier.padding(8.dp))

            SelectOrderTypeCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ArrowDropDown,
                contentDescription = stringResource(R.string.board_dropdown_menu_order_by_descending),
                onClick = { onConfirmOrder(OrderType.DESCENDING) }
            )
        }
    }
}

@Composable
private fun SelectOrderTypeCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.height(40.dp),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.primary
        ),
        onClick = { }
    ) {
        IconButton(
            onClick = onClick
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
            )
        }
    }
}

