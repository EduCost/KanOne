package com.educost.kanone.presentation.screens.logs.logdetail

import android.content.ClipData
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.educost.kanone.R
import com.educost.kanone.presentation.components.NavigateBackIcon
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun LogDetailScreen(
    modifier: Modifier = Modifier,
    logEventJson: String,
    onNavigateBack: () -> Unit,
    viewModel: LogDetailViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.parseLogEvent(logEventJson)
    }

    val clipData = ClipData.newPlainText("Log Event", logEventJson)
    val clipEntry = ClipEntry(clipData)
    val clipBoard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val logEvent by viewModel.logEvent.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            LogDetailAppBar(
                onNavigateBack = onNavigateBack,
                onCopy = { scope.launch { clipBoard.setClipEntry(clipEntry) } }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            logEvent?.let { log ->

                if (log.message != null) {
                    LogDetailCard(title = "Message", content = log.message)
                    Spacer(Modifier.padding(bottom = 8.dp))
                }

                LogDetailCard(title = "StackTrace", content = log.stackTrace)
                Spacer(Modifier.padding(bottom = 8.dp))

                LogDetailCard(title = "From", content = log.from)
                Spacer(Modifier.padding(bottom = 8.dp))

                LogDetailCard(title = "App Version", content = log.appVersionName)
                Spacer(Modifier.padding(bottom = 8.dp))

                LogDetailCard(title = "Level", content = log.level.name)
                Spacer(Modifier.padding(bottom = 8.dp))

                LogDetailCard(title = "Device Sdk Int", content = log.deviceSdkInt.toString())
                Spacer(Modifier.padding(bottom = 8.dp))


                val formattedDate = remember {
                    val instant = Instant.parse(log.timestamp)
                    val formatter =
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault())
                    formatter.format(instant)
                }
                LogDetailCard(title = "Timestamp", content = formattedDate)

            }
        }
    }
}

@Composable
private fun LogDetailCard(modifier: Modifier = Modifier, title: String, content: String) {
    Card(
        modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        SelectionContainer {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )
                Text(text = content, style = MaterialTheme.typography.labelLarge)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogDetailAppBar(modifier: Modifier = Modifier, onNavigateBack: () -> Unit, onCopy: () -> Unit) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(text = "Log Detail")
        },
        navigationIcon = {
            NavigateBackIcon { onNavigateBack() }
        },
        actions = {
            IconButton(
                onClick = onCopy
            ) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = stringResource(R.string.copy_button_content_description)
                )
            }
        }

    )
}



