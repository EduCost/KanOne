package com.educost.kanone.presentation.screens.board

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.educost.kanone.domain.model.Board
import com.educost.kanone.presentation.theme.KanOneTheme

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    viewModel: BoardViewModel = hiltViewModel(),
    boardId: Long
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { }
    BoardScreen(
        modifier = modifier,
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    state: BoardState,
    onIntent: (BoardIntent) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
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
                board = Board(
                    id = 0,
                    name = "Dummy",
                )
            ),
            onIntent = {}
        )
    }
}