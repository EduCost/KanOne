package com.educost.kanone.presentation.screens.board

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.educost.kanone.domain.model.Board
import com.educost.kanone.presentation.model.BoardUi
import com.educost.kanone.presentation.screens.board.components.BoardAppBar
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
        topBar = {
            BoardAppBar(
                boardName = state.board?.name,
                type = state.topBarType,
                onIntent = onIntent
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->

        LazyRow(
            modifier = Modifier.padding(innerPadding)
        ) {

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BoardScreenPreview() {
    KanOneTheme {
        BoardScreen(
            state = BoardState(
                board = BoardUi(
                    id = 0,
                    name = "Dummy",
                    emptyList()
                )
            ),
            onIntent = {},
            snackBarHostState = SnackbarHostState()

        )
    }
}