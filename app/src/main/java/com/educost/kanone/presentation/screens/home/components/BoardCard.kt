package com.educost.kanone.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.presentation.theme.KanOneTheme
import java.time.LocalDateTime

@Composable
fun BoardCard(modifier: Modifier = Modifier, board: Board, onNavigateToBoard: (Long) -> Unit) {
    Box(
        modifier = modifier
            .height(250.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(onClick = { onNavigateToBoard(board.id) }),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            board.columns.forEach { column ->
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(MaterialTheme.shapes.small)
                        .width(100.dp)
                        .heightIn(min = 55.dp)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Column(
                        modifier = Modifier.padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.surfaceVariant)

                        )

                        Spacer(Modifier.height(0.dp))

                        column.cards.forEach { card ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = board.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun BoardCardPreview() {
    KanOneTheme {
        BoardCard(
            board = Board(
                id = 0,
                name = "Test",
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
            ),
            onNavigateToBoard = {}
        )
    }
}
