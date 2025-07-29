package com.educost.kanone.presentation.screens.board

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.screens.board.components.AddColumn
import com.educost.kanone.presentation.screens.board.components.BoardAppBar
import com.educost.kanone.presentation.screens.board.components.BoardAppBarType
import com.educost.kanone.presentation.theme.KanOneTheme
import com.educost.kanone.presentation.util.ObserveAsEvents
import kotlinx.coroutines.launch

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    viewModel: BoardViewModel = hiltViewModel(),
    boardId: Long
) {

    LaunchedEffect(Unit) {
        viewModel.onIntent(BoardIntent.ObserveBoard(boardId))
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ObserveAsEvents(flow = viewModel.sideEffectFlow) { event ->
        when (event) {
            is BoardSideEffect.ShowSnackBar -> {
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


    BoardScreen(
        modifier = modifier,
        state = state,
        onIntent = viewModel::onIntent,
        snackBarHostState = snackBarHostState
    )
}

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    state: BoardState,
    onIntent: (BoardIntent) -> Unit,
    snackBarHostState: SnackbarHostState
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            state.board?.let { board ->
                BoardAppBar(
                    boardName = board.name,
                    type = state.topBarType,
                    onIntent = onIntent
                )
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->

        state.board?.let { board ->
            LazyRow(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(board.columns) { column ->

                }

                item {
                    AddColumn(
                        state = state,
                        onIntent = onIntent
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun BoardScreenPreview() {
    KanOneTheme {
        BoardScreen(
            state = BoardState(
                board = BoardUi(
                    id = 0,
                    name = "Board name",
                    emptyList()
                ),
                topBarType = BoardAppBarType.DEFAULT
            ),
            onIntent = {},
            snackBarHostState = SnackbarHostState()

        )
    }
}