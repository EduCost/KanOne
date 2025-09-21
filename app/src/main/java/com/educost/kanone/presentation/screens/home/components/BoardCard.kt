package com.educost.kanone.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educost.kanone.R
import com.educost.kanone.domain.model.Board
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.domain.model.KanbanColumn
import com.educost.kanone.presentation.theme.KanOneTheme
import com.educost.kanone.presentation.theme.ThemeData
import com.educost.kanone.presentation.theme.ThemeType
import java.time.LocalDateTime

@Composable
fun BoardCard(
    modifier: Modifier = Modifier,
    board: Board,
    onNavigateToBoard: () -> Unit,
    onRenameBoard: () -> Unit,
    onDeleteBoard: () -> Unit
) {
    Box(
        modifier = modifier
            .height(230.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
            .clickable(onClick = { onNavigateToBoard() }),
    ) {

        if (board.columns.isEmpty()) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.home_board_card_empty_board),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                BoardCardBottomRow(
                    name = board.name,
                    onRenameBoard = onRenameBoard,
                    onDeleteBoard = onDeleteBoard
                )
            }

        } else {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                BoardCardColumns(
                    columns = board.columns
                )
            }
            BoardCardBottomRow(
                modifier = Modifier.align(Alignment.BottomStart),
                name = board.name,
                onRenameBoard = onRenameBoard,
                onDeleteBoard = onDeleteBoard
            )
        }


    }
}


@Composable
private fun BoardCardColumns(modifier: Modifier = Modifier, columns: List<KanbanColumn>) {
    columns.forEach { column ->
        Box(
            modifier = modifier
                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                .clip(MaterialTheme.shapes.small)
                .width(70.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))

                )

                Spacer(Modifier.height(0.dp))

                column.cards.forEach { card ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    )
                }
            }
        }
    }
}


@Composable
private fun BoardCardBottomRow(
    modifier: Modifier = Modifier,
    name: String,
    onRenameBoard: () -> Unit,
    onDeleteBoard: () -> Unit
) {

    var isDropdownMenuOpen by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
            .padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = { isDropdownMenuOpen = true }
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.home_board_card_more_options_content_description),
                tint = MaterialTheme.colorScheme.onSurface
            )
            BoardCardDropdownMenu(
                expanded = isDropdownMenuOpen,
                onDismissRequest = { isDropdownMenuOpen = false },
                onRename = {
                    onRenameBoard()
                    isDropdownMenuOpen = false
                },
                onDelete = {
                    onDeleteBoard()
                    isDropdownMenuOpen = false
                }
            )
        }
    }
}


@PreviewDynamicColors
@Composable
private fun BoardCardPreview() {
    KanOneTheme {
        BoardCard(
            board = Board(
                id = 0,
                name = "Board Name",
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
                    KanbanColumn(
                        id = 0,
                        name = "Column 1",
                        position = 0
                    ),
                )
            ),
            onNavigateToBoard = {},
            onDeleteBoard = {},
            onRenameBoard = {}
        )
    }
}

@PreviewDynamicColors
@Composable
private fun BoardCardPreviewDark() {
    KanOneTheme(themeData = ThemeData(ThemeType.DARK)) {
        BoardCard(
            board = Board(
                id = 0,
                name = "Board Name",
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
                    KanbanColumn(
                        id = 0,
                        name = "Column 1",
                        position = 0
                    ),
                )
            ),
            onNavigateToBoard = {},
            onDeleteBoard = {},
            onRenameBoard = {}
        )
    }
}
