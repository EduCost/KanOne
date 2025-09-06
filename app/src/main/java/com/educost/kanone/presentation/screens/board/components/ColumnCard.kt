package com.educost.kanone.presentation.screens.board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.educost.kanone.domain.model.Label
import com.educost.kanone.domain.model.Task
import com.educost.kanone.presentation.components.LabelChip
import com.educost.kanone.presentation.screens.board.model.CardUi
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.theme.KanOneTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ColumnCard(modifier: Modifier = Modifier, card: CardUi) {

    val context = LocalContext.current

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(7.dp))
    ) {
        card.thumbnailFileName?.let { cover ->
            val imageRequest = remember(cover, context) {
                ImageRequest.Builder(context)
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .memoryCachePolicy(CachePolicy.DISABLED)
                    .data(cover)
                    .build()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 150.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small),
                    model = imageRequest,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    error = rememberVectorPainter(Icons.Filled.Error),
                    placeholder = rememberVectorPainter(Icons.Filled.Image)
                )
            }
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {

                Text(
                    text = card.title,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (card.labels.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        card.labels.forEach { label ->
                            LabelChip(label = label, smallVersion = true)
                        }
                    }
                }

                if (card.tasks.isNotEmpty()) {
                    val taskAmount = remember { card.tasks.size }
                    val completedTasksAmount = remember { card.tasks.count { it.isCompleted } }

                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { completedTasksAmount.toFloat() / taskAmount },
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.secondaryContainer,
                            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            modifier = Modifier.size(12.dp),
                            imageVector = Icons.Filled.DoneAll,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "$completedTasksAmount/$taskAmount",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                val shouldShowBottomItems = remember {
                    card.dueDate != null || card.attachments.isNotEmpty() ||
                            card.description != null && card.description.isNotBlank()
                }

                if (shouldShowBottomItems) {

                    val attachmentAmount = remember { card.attachments.size }

                    HorizontalDivider(Modifier.padding(top = 8.dp, bottom = 12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        card.dueDate?.let { dueDate ->
                            val formatedDate = remember {
                                dueDate.format(
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = formatedDate,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        if (card.description != null && card.description.isNotBlank()) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Subject,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Filled.AttachFile,
                            contentDescription = null
                        )
                        Text(attachmentAmount.toString())
                    }
                }
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun ColumnCardPreview() {
    KanOneTheme {
        Surface {
            ColumnCard(
                modifier = Modifier.padding(32.dp),
                card = CardUi(
                    id = 0,
                    title = "Card title",
                    position = 0,
                    color = null,
                    description = "Some description",
                    dueDate = LocalDateTime.now().plusDays(3),
                    createdAt = LocalDateTime.now(),
                    thumbnailFileName = null,
                    tasks = listOf(
                        Task(
                            id = 0,
                            description = "Example",
                            isCompleted = false,
                            position = 0
                        ),
                        Task(
                            id = 1,
                            description = "Completed task",
                            isCompleted = true,
                            position = 1
                        )
                    ),
                    attachments = emptyList(),
                    labels = listOf(
                        Label(
                            id = 0,
                            name = "Label",
                            color = null
                        ),
                        Label(
                            id = 1,
                            name = "Another label",
                            color = -4221
                        )
                    ),
                    coordinates = Coordinates()
                ),
            )
        }
    }
}