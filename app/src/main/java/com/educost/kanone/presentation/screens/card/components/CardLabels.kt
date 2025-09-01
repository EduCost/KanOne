package com.educost.kanone.presentation.screens.card.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
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
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.domain.model.Label
import com.educost.kanone.presentation.screens.card.CardIntent

@Composable
fun CardLabels(
    modifier: Modifier = Modifier,
    labels: List<Label>,
    boardLabels: List<Label>,
    isMenuExpanded: Boolean,
    onIntent: (CardIntent) -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                1.dp
            )
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Label,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(text = stringResource(R.string.card_labels))

                Spacer(Modifier.weight(1f))

                IconButton(
                    onClick = {
                        onIntent(CardIntent.OpenLabelPicker)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.card_add_label_button_content_description)
                    )
                    CardLabelPicker(
                        labels = boardLabels,
                        selectedLabels = labels,
                        expanded = isMenuExpanded,
                        onDismiss = {
                            onIntent(CardIntent.CloseLabelPicker)
                        },
                        onCreateLabel = {
                            onIntent(CardIntent.StartCreatingLabel)
                        },
                        onLabelSelected = {
                            onIntent(CardIntent.UpdateLabelAssociation(it))
                        }
                    )
                }

            }

            LazyRow(
                contentPadding = PaddingValues(bottom = 8.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(labels) { label ->
                    LabelChip(
                        label = label,
                        onClick = {onIntent(CardIntent.StartEditingLabel(label))}
                    )
                }
            }
        }
    }
}