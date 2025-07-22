package com.educost.kanone.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.educost.kanone.domain.model.Board
import com.educost.kanone.presentation.theme.KanOneTheme

@Composable
fun BoardCard(modifier: Modifier = Modifier, board: Board, onNavigateToBoard: (Long) -> Unit) {
    Card(
        modifier = modifier,
        onClick = { onNavigateToBoard(board.id) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp,)
        ) {
            Text(
                text = board.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BoardCardPreview() {
    KanOneTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            BoardCard(
                modifier = Modifier.fillMaxWidth(0.5f),
                board = Board(0, "Test"),
                onNavigateToBoard = {}
            )
        }
    }
}