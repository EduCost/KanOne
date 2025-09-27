package com.educost.kanone.presentation.screens.settings.root

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.educost.kanone.R
import com.educost.kanone.presentation.components.SettingItem
import com.educost.kanone.presentation.screens.settings.components.SettingsTopBar
import com.educost.kanone.presentation.theme.KanOneTheme
import com.educost.kanone.presentation.util.ObserveAsEvents
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateToSettingsTheme: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToLog: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.sideEffectFlow) { event ->
        when (event) {
            is SettingsRootSideEffect.OnNavigateBack -> onNavigateBack()

            is SettingsRootSideEffect.OnNavigateToSettingsTheme -> onNavigateToSettingsTheme()

            is SettingsRootSideEffect.OnNavigateToAbout -> onNavigateToAbout()

            is SettingsRootSideEffect.OnNavigateToLog -> onNavigateToLog()

            is SettingsRootSideEffect.ShowSnackBar -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()

                    val result = snackBarHostState.showSnackbar(
                        message = event.snackbarEvent.message.asString(context),
                        actionLabel = event.snackbarEvent.action?.label?.asString(context),
                        duration = event.snackbarEvent.duration
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        event.snackbarEvent.action?.action?.invoke()
                    }
                }
            }
        }
    }

    SettingsScreen(
        modifier = modifier,
        state = state,
        onIntent = viewModel::onIntent,
        snackBarHostState = snackBarHostState
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    state: SettingsUiState,
    onIntent: (SettingsIntent) -> Unit,
    snackBarHostState: SnackbarHostState
) {

    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val toast = remember {
        Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            SettingsTopBar(
                title = stringResource(R.string.settings_appbar_title),
                onNavigateBack = { onIntent(SettingsIntent.OnNavigateBack) },
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
                title = stringResource(R.string.settings_theme),
                icon = Icons.Filled.ColorLens,
                showIconBackground = true,
                hasEndIcon = true,
                onClick = { onIntent(SettingsIntent.OnNavigateToSettingsTheme) }
            )
            SettingItem(
                title = "Language",
                icon = Icons.Filled.Language,
                showIconBackground = true,
                hasEndIcon = true,
                onClick = {
                    toast.cancel()
                    toast.show()
                }
            )
            SettingItem(
                title = "Data",
                icon = Icons.Filled.Archive,
                showIconBackground = true,
                hasEndIcon = true,
                onClick = {
                    toast.cancel()
                    toast.show()
                }
            )
            SettingItem(
                title = stringResource(R.string.settings_about),
                icon = Icons.Filled.Info,
                showIconBackground = true,
                hasEndIcon = true,
                onClick = { onIntent(SettingsIntent.OnNavigateToAbout) }
            )
            SettingItem(
                title = stringResource(R.string.settings_logs),
                icon = Icons.AutoMirrored.Filled.ListAlt,
                showIconBackground = true,
                hasEndIcon = true,
                onClick = { onIntent(SettingsIntent.OnNavigateToLog) }
            )

        }

    }
}


@PreviewLightDark
@Composable
private fun SettingsScreenPreview() {
    KanOneTheme {
        SettingsScreen(
            state = SettingsUiState(),
            onIntent = { },
            snackBarHostState = remember { SnackbarHostState() }
        )
    }
}