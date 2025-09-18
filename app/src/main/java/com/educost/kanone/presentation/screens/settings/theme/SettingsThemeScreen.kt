package com.educost.kanone.presentation.screens.settings.theme

import android.app.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.educost.kanone.R
import com.educost.kanone.presentation.screens.settings.components.SettingsSideEffect
import com.educost.kanone.presentation.screens.settings.components.SettingsTopBar
import com.educost.kanone.presentation.theme.KanOneTheme
import com.educost.kanone.presentation.theme.ThemeType
import com.educost.kanone.presentation.util.ObserveAsEvents
import com.educost.kanone.presentation.util.UiText
import kotlinx.coroutines.launch

val rowPadding = 16.dp

@Composable
fun SettingsThemeScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    viewModel: SettingsThemeViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.sideEffectFlow) { event ->
        when (event) {
            is SettingsSideEffect.OnNavigateBack -> onNavigateBack()

            is SettingsSideEffect.ShowSnackBar -> {
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

    SettingsThemeScreen(
        modifier = modifier,
        state = state,
        onIntent = viewModel::onIntent,
        snackBarHostState = snackBarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsThemeScreen(
    modifier: Modifier = Modifier,
    state: SettingsThemeUiState,
    onIntent: (SettingsThemeIntent) -> Unit,
    snackBarHostState: SnackbarHostState
) {

    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var isSelectingTheme by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            SettingsTopBar(
                title = stringResource(R.string.settings_theme),
                onNavigateBack = { onIntent(SettingsThemeIntent.OnNavigateBack) },
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
            DarkMode(
                themeType = state.themeType,
                onClick = { isSelectingTheme = true },
                onDarkModeChange = { onIntent(SettingsThemeIntent.SetDarkMode(it)) }
            )
        }

        if (isSelectingTheme) {
            ThemeDialog(
                selectedTheme = state.themeType,
                onThemeSelected = { onIntent(SettingsThemeIntent.SetTheme(it)) },
                onDismiss = { isSelectingTheme = false }
            )
        }
    }
}

@Composable
private fun DarkMode(
    modifier: Modifier = Modifier,
    themeType: ThemeType,
    onClick: () -> Unit,
    onDarkModeChange: (Boolean) -> Unit
) {

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val isAppInDarkTheme = remember(themeType) {
        themeType == ThemeType.SYSTEM && isSystemInDarkTheme || themeType == ThemeType.DARK
    }
    val themeText = remember(themeType) {
        when (themeType) {
            ThemeType.SYSTEM -> UiText.StringResource(R.string.settings_theme_dark_mode_system)
            ThemeType.LIGHT -> UiText.StringResource(R.string.settings_theme_dark_mode_off)
            ThemeType.DARK -> UiText.StringResource(R.string.settings_theme_dark_mode_on)
        }
    }

    Row(
        modifier = modifier
            .height(76.dp)
            .clickable(onClick = onClick)
            .padding(rowPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.DarkMode,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = stringResource(R.string.settings_theme_dark_mode),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = themeText.asString(),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        VerticalDivider(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(32.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceContainerHighest
        )

        Switch(
            checked = isAppInDarkTheme,
            onCheckedChange = { onDarkModeChange(it) }
        )
    }
}


@Composable
private fun ThemeDialog(
    modifier: Modifier = Modifier,
    selectedTheme: ThemeType,
    onThemeSelected: (ThemeType) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.ColorLens,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(R.string.settings_theme_dialog_title))
        },
        text = {
            Column {
                ThemeRadioButton(
                    text = stringResource(R.string.settings_theme_dialog_option_system),
                    selected = selectedTheme == ThemeType.SYSTEM,
                    onClick = { onThemeSelected(ThemeType.SYSTEM) }
                )
                ThemeRadioButton(
                    text = stringResource(R.string.settings_theme_dialog_option_light),
                    selected = selectedTheme == ThemeType.LIGHT,
                    onClick = { onThemeSelected(ThemeType.LIGHT) }
                )
                ThemeRadioButton(
                    text = stringResource(R.string.settings_theme_dialog_option_dark),
                    selected = selectedTheme == ThemeType.DARK,
                    onClick = { onThemeSelected(ThemeType.DARK) }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.settings_theme_dialog_close))
            }
        }
    )
}

@Composable
fun ThemeRadioButton(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@PreviewLightDark
@Composable
private fun SettingsThemeScreenPreview() {
    KanOneTheme {
        SettingsThemeScreen(
            state = SettingsThemeUiState(),
            onIntent = { },
            snackBarHostState = remember { SnackbarHostState() }
        )

    }
}