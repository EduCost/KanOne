package com.educost.kanone.presentation.screens.logs.loglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.educost.kanone.R
import com.educost.kanone.domain.logs.LogEvent
import com.educost.kanone.domain.logs.LogLevel
import com.educost.kanone.presentation.screens.logs.loglist.components.DeleteLogsDialog
import com.educost.kanone.presentation.screens.logs.loglist.components.LogAppBar
import com.educost.kanone.presentation.theme.KanOneTheme
import com.educost.kanone.presentation.util.ObserveAsEvents
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun LogScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateToLogDetail: (String) -> Unit,
    viewModel: LogViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.sideEffectFlow) { event ->
        when (event) {
            is LogSideEffect.OnNavigateBack -> onNavigateBack()

            is LogSideEffect.OnNavigateToLogDetail -> {
                onNavigateToLogDetail(event.logJson)
            }

            is LogSideEffect.ShowSnackBar -> {
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

    LogScreen(
        modifier = modifier,
        state = state,
        onIntent = viewModel::onIntent,
        snackBarHostState = snackBarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    modifier: Modifier = Modifier,
    state: LogUiState,
    onIntent: (LogIntent) -> Unit,
    snackBarHostState: SnackbarHostState
) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var isDeletingLogs by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            LogAppBar(
                title = stringResource(R.string.settings_logs),
                onNavigateBack = { onIntent(LogIntent.OnNavigateBack) },
                scrollBehavior = scrollBehavior,
                onDelete = { isDeletingLogs = true }
            )
        }
    ) { innerPadding ->
        if (state.logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Logs are empty",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.logs) { log ->
                    LogCard(
                        log = log,
                        onNavigateToLogDetail = {
                            onIntent(LogIntent.OnNavigateToLogDetail(log))
                        }
                    )
                }
            }
        }

        if (isDeletingLogs) {
            DeleteLogsDialog(
                onDismiss = { isDeletingLogs = false },
                onConfirm = {
                    isDeletingLogs = false
                    onIntent(LogIntent.DeleteAllLogs)
                }
            )
        }
    }

}

@Composable
private fun LogCard(
    modifier: Modifier = Modifier,
    log: LogEvent,
    onNavigateToLogDetail: () -> Unit
) {

    val errorText = remember { log.message ?: log.exceptionName }
    val formattedDate = remember {
        val instant = Instant.parse(log.timestamp)
        val formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault())
        formatter.format(instant)
    }

    Card(
        modifier = modifier,
        onClick = onNavigateToLogDetail,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(min = 56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = errorText,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(Modifier.width(16.dp))

            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}


@PreviewLightDark
@Composable
private fun LogScreenPreview() {
    KanOneTheme {
        LogScreen(
            state = LogUiState(
                logs = listOf(
                    LogEvent(
                        timestamp = Instant.now().toString(),
                        exceptionName = "ArithmeticException",
                        message = "Some Error Message",
                        stackTrace = "",
                        level = LogLevel.ERROR,
                        from = "",
                        deviceSdkInt = 36,
                        appVersionName = "1.0"
                    ),
                    LogEvent(
                        timestamp = Instant.now().toString(),
                        exceptionName = "NullPointerException",
                        message = null,
                        stackTrace = "",
                        level = LogLevel.ERROR,
                        from = "",
                        deviceSdkInt = 36,
                        appVersionName = "1.0"
                    ),
                )
            ),
            onIntent = {},
            snackBarHostState = remember { SnackbarHostState() }
        )
    }
}