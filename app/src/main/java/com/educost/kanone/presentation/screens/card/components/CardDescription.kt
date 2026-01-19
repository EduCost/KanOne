package com.educost.kanone.presentation.screens.card.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.presentation.theme.KanOneTheme
import com.educost.kanone.presentation.util.MarkdownRenderer
import com.educost.kanone.presentation.util.markdownTypographySmall
import com.mikepenz.markdown.model.rememberMarkdownState

@Composable
fun CardDescription(
    modifier: Modifier = Modifier,
    cardDescription: String,
    onClick: () -> Unit,
) {



    val markdownState = rememberMarkdownState(cardDescription)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        )
    ) {
        Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null
                )

                Spacer(Modifier.width(8.dp))
                Text(text = stringResource(R.string.card_description))
                Spacer(Modifier.weight(1f))

                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.card_edit_description_button_content_description)
                    )
                }
            }

            if (cardDescription.isNotBlank()) {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable(onClick = onClick)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(8.dp)
                ) {
                    MarkdownRenderer(
                        modifier = Modifier.heightIn(max = 250.dp),
                        markdownState = markdownState,
                        typography = markdownTypographySmall()
                    )
                }
            }

        }
    }
}

@PreviewLightDark
@Composable
private fun CardDescriptionPreview() {
    KanOneTheme {
        CardDescription(
            cardDescription = "Card description",
            onClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun CardDescriptionEmptyPreview() {
    KanOneTheme {
        CardDescription(
            cardDescription = "",
            onClick = {}
        )
    }
}