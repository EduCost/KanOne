package com.educost.kanone.presentation.screens.logs.loglist.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.educost.kanone.R
import com.educost.kanone.presentation.components.NavigateBackIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogAppBar(
    modifier: Modifier = Modifier,
    title: String,
    onNavigateBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    onDelete: () -> Unit
) {
    LargeTopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = { Text(title) },
        navigationIcon = {
            NavigateBackIcon { onNavigateBack() }
        },
        actions = {
            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Filled.DeleteOutline,
                    contentDescription = stringResource(R.string.delete_button_content_description)
                )
            }
        }
    )
}