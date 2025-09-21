package com.educost.kanone.presentation.screens.settings.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Copyright
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.educost.kanone.BuildConfig
import com.educost.kanone.R
import com.educost.kanone.presentation.screens.settings.components.SettingItem
import com.educost.kanone.presentation.screens.settings.components.SettingsTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateToCredits: () -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()
    val appVersion = remember { BuildConfig.VERSION_NAME }

    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(
                title = stringResource(R.string.settings_about),
                onNavigateBack = onNavigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(scrollState)
        ) {
            SettingItem(
                title = stringResource(R.string.settings_credits),
                subtitle = stringResource(R.string.settings_about_credits_subtitle),
                icon = Icons.Filled.Copyright,
                onClick = onNavigateToCredits,
                hasEndIcon = true
            )
            SettingItem(
                title = stringResource(R.string.settings_about_version),
                subtitle = appVersion,
                icon = Icons.Outlined.Info,
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun AboutScreenPreview() {
    AboutScreen(onNavigateBack = {}, onNavigateToCredits = {})
}