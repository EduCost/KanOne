package com.educost.kanone.presentation.screens.settings.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.educost.kanone.presentation.components.NavigateBackIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(modifier: Modifier = Modifier, title: String, onNavigateBack: () -> Unit, scrollBehavior: TopAppBarScrollBehavior) {
    LargeTopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = { Text(title) },
        navigationIcon = { NavigateBackIcon { onNavigateBack() } }
    )
}