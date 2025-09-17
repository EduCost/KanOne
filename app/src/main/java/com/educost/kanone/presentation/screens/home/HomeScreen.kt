package com.educost.kanone.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.educost.kanone.R
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.presentation.screens.home.components.BoardCard
import com.educost.kanone.presentation.screens.home.components.CreateBoardDialog
import com.educost.kanone.presentation.screens.home.components.HomeTopBar
import com.educost.kanone.presentation.theme.KanOneTheme
import com.educost.kanone.presentation.util.ObserveAsEvents
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToBoard: (Long) -> Unit
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ObserveAsEvents(flow = viewModel.sideEffectFlow) { event ->
        when (event) {
            is HomeSideEffect.NavigateToBoardScreen -> onNavigateToBoard(event.boardId)
            is HomeSideEffect.ShowSnackBar -> {
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

    HomeScreen(
        modifier = modifier,
        state = state,
        onIntent = viewModel::onIntent,
        snackBarHostState = snackBarHostState
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HomeTopBar()
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.home_fab_create_board)) },
                icon = { Icon(Icons.Filled.Add, null) },
                onClick = {
                    onIntent(HomeIntent.ShowCreateBoardDialog)
                },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            when {
                state.isLoading -> LinearProgressIndicator(Modifier.fillMaxWidth())

                state.boards.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.home_screen_no_boards_found))
                    }
                }

                else -> {
                    val fabHeight = remember { 72.dp }
                    LazyColumn(
                        modifier = Modifier,
                        contentPadding = PaddingValues(
                            top = 12.dp,
                            start = 12.dp,
                            end = 12.dp,
                            bottom = fabHeight + 12.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(state.boards) { board ->
                            BoardCard(
                                board = board,
                                onNavigateToBoard = {
                                    onIntent(HomeIntent.NavigateToBoardScreen(board.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (state.showCreateBoardDialog) {
        CreateBoardDialog(
            state = state,
            onIntent = onIntent
        )
    }
}

@PreviewLightDark
@Composable
private fun HomeScreenPreview() {
    KanOneTheme {
        HomeScreen(
            state = HomeUiState(
                boards = listOf(
                    Board(
                        id = 0,
                        name = "Demo",
                        columns = listOf(
                            KanbanColumn(
                                id = 0,
                                name = "Column 1",
                                position = 0,
                                cards = listOf(
                                    CardItem(
                                        id = 0,
                                        title = "Card 1",
                                        position = 0,
                                        createdAt = LocalDateTime.now()
                                    ),
                                    CardItem(
                                        id = 1,
                                        title = "Card 2",
                                        position = 1,
                                        createdAt = LocalDateTime.now()
                                    ),
                                    CardItem(
                                        id = 2,
                                        title = "Card 3",
                                        position = 2,
                                        createdAt = LocalDateTime.now()
                                    ),
                                    CardItem(
                                        id = 3,
                                        title = "Card 4",
                                        position = 3,
                                        createdAt = LocalDateTime.now()
                                    ),
                                )
                            ),
                            KanbanColumn(
                                id = 0,
                                name = "Column 1",
                                position = 0,
                                cards = listOf(
                                    CardItem(
                                        id = 0,
                                        title = "Card 1",
                                        position = 0,
                                        createdAt = LocalDateTime.now()
                                    ),
                                    CardItem(
                                        id = 1,
                                        title = "Card 2",
                                        position = 1,
                                        createdAt = LocalDateTime.now()
                                    )
                                )
                            ),
                        )
                    )
                )
            ),
            onIntent = {},
            snackBarHostState = SnackbarHostState()
        )
    }
}
