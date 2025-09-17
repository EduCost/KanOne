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
import com.educost.kanone.domain.model.Attachment
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

    val cardProperties = remember { getCardProperties(card) }

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(7.dp))
    ) {
        card.coverFileName?.let { cover ->
            CardImage(
                cover = cover,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 150.dp)
            )
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
                    color = MaterialTheme.colorScheme.onSurface,
                )

                if (cardProperties.hasLabels) {
                    CardLabels(
                        modifier = Modifier.padding(top = 12.dp),
                        labels = card.labels
                    )
                }


                if (cardProperties.hasTasks) {
                    val taskAmount = remember { card.tasks.size }
                    val completedTasksAmount = remember { card.tasks.count { it.isCompleted } }

                    CardTasks(
                        modifier = Modifier.padding(top = 8.dp),
                        taskAmount = taskAmount,
                        completedTasksAmount = completedTasksAmount
                    )
                }

                if (cardProperties.hasHorizontalDivider) {
                    HorizontalDivider(Modifier.padding(top = 8.dp, bottom = 12.dp))
                }

                CardBottomRow(
                    cardProperties = cardProperties,
                    card = card
                )
            }
        }
    }
}

@Composable
private fun CardImage(modifier: Modifier = Modifier, cover: String) {
    val context = LocalContext.current
    val imageRequest = remember {
        ImageRequest.Builder(context)
            .diskCachePolicy(CachePolicy.DISABLED)
            .data(cover)
            .build()
    }

    Box(
        modifier = modifier,
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

@Composable
private fun CardLabels(modifier: Modifier = Modifier, labels: List<Label>) {
    FlowRow(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEach { label ->
            LabelChip(label = label, smallVersion = true)
        }
    }
}

@Composable
private fun CardTasks(modifier: Modifier = Modifier, taskAmount: Int, completedTasksAmount: Int) {
    Row(
        modifier = modifier,
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
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "$completedTasksAmount/$taskAmount",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CardDueDate(modifier: Modifier = Modifier, dueDate: LocalDateTime) {
    val formatedDate = remember {
        dueDate.format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
    }
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = formatedDate,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun CardDescriptionIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.Subject,
        contentDescription = null,
        modifier = modifier.size(20.dp),
        tint = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun CardAttachments(modifier: Modifier = Modifier, attachmentAmount: String) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            imageVector = Icons.Filled.AttachFile,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = attachmentAmount,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CardBottomRow(
    modifier: Modifier = Modifier,
    cardProperties: CardProperties,
    card: CardUi
) {

    val paddingTop = remember {
        if (cardProperties.hasHorizontalDivider || cardProperties.cardIsEmpty) {
            0.dp
        } else if (cardProperties.hasLabels) {
            12.dp
        } else {
            8.dp
        }
    }

    Row(
        modifier = modifier.padding(top = paddingTop),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (cardProperties.hasDescription) {
            CardDescriptionIcon()
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (cardProperties.hasAttachments) {
            val attachmentAmount = remember { card.attachments.size.toString() }
            CardAttachments(attachmentAmount = attachmentAmount)
        }

        Spacer(modifier = Modifier.weight(1f))

        card.dueDate?.let { dueDate ->
            CardDueDate(
                dueDate = dueDate
            )
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
                    coverFileName = null,
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
                    attachments = listOf(
                        Attachment(
                            id = 0,
                            fileName = ""
                        )
                    ),
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


private data class CardProperties(
    val hasDescription: Boolean,
    val hasDueDate: Boolean,
    val hasLabels: Boolean,
    val hasTasks: Boolean,
    val hasAttachments: Boolean,
) {
    val hasHorizontalDivider = hasTasks && hasLabels &&
            (hasDescription || hasDueDate || hasAttachments)

    val cardIsEmpty = !hasDescription && !hasDueDate && !hasLabels && !hasTasks && !hasAttachments
}

private fun getCardProperties(card: CardUi): CardProperties {
    return CardProperties(
        hasDescription = card.description != null && card.description.isNotBlank(),
        hasDueDate = card.dueDate != null,
        hasLabels = card.labels.isNotEmpty(),
        hasTasks = card.tasks.isNotEmpty(),
        hasAttachments = card.attachments.isNotEmpty()
    )
}