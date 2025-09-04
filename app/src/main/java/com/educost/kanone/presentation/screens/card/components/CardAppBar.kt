package com.educost.kanone.presentation.screens.card.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.presentation.components.ActionTopBar
import com.educost.kanone.presentation.screens.card.CardIntent
import com.educost.kanone.presentation.screens.card.utils.CardAppBarType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardAppBar(
    modifier: Modifier = Modifier,
    card: CardItem,
    onIntent: (CardIntent) -> Unit,
    type: CardAppBarType,
    scrollBehavior: TopAppBarScrollBehavior
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    when (type) {

        CardAppBarType.DEFAULT -> {

            var isShowingDropdownMenu by rememberSaveable { mutableStateOf(false) }

            TopAppBar(
                modifier = modifier,
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ),
                title = {
                    Text(text = card.title)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onIntent(CardIntent.OnNavigateBack) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back_button_content_description)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isShowingDropdownMenu = true }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.icon_more_vert_content_description)
                        )

                        DropdownMenu(
                            expanded = isShowingDropdownMenu,
                            onDismissRequest = { isShowingDropdownMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(R.string.card_appbar_dropdown_menu_delete_card))
                                },
                                onClick = {
                                    isShowingDropdownMenu = false
                                    onIntent(CardIntent.DeleteCard)
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
                                )
                            )
                        }
                    }
                }
            )
        }

        CardAppBarType.DESCRIPTION -> ActionTopBar(
            title = stringResource(R.string.card_appbar_save_description),
            leftIconContentDescription = stringResource(R.string.card_appbar_cancel_description_content_description),
            onLeftIconClick = { onIntent(CardIntent.CancelEditingDescription) },
            rightIcon = Icons.Default.Save,
            rightIconContentDescription = stringResource(R.string.card_appbar_save_description_content_description),
            onRightIconClick = { onIntent(CardIntent.SaveDescription) }
        )

        CardAppBarType.ADD_TASK -> ActionTopBar(
            title = stringResource(R.string.card_appbar_create_task),
            leftIconContentDescription = stringResource(R.string.card_appbar_cancel_task_creation_content_description),
            onLeftIconClick = { onIntent(CardIntent.CancelCreatingTask) },
            rightIconContentDescription = stringResource(R.string.card_appbar_create_task_content_description),
            onRightIconClick = {
                keyboardController?.hide()
                onIntent(CardIntent.ConfirmTaskCreation)
            }
        )

        CardAppBarType.EDIT_TASK -> ActionTopBar(
            title = stringResource(R.string.card_appbar_edit_task),
            leftIconContentDescription = stringResource(R.string.card_appbar_cancel_task_edit_content_description),
            onLeftIconClick = { onIntent(CardIntent.CancelEditingTask) },
            rightIconContentDescription = stringResource(R.string.card_appbar_save_task_changes_content_description),
            rightIcon = Icons.Default.Save,
            onRightIconClick = {
                keyboardController?.hide()
                onIntent(CardIntent.ConfirmTaskEdit)
            }
        )
    }
}