package com.educost.kanone.presentation.screens.card.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.educost.kanone.R


@Composable
fun CardDueDate(
    modifier: Modifier = Modifier,
    dueDate: String?,
    onDueDateClick: () -> Unit,
    onRemoveDueDate: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                1.dp
            )
        )
    ) {

        Box(
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {


            dueDate?.let { dueDate ->

                DueDate(
                    dueDate = dueDate,
                    onDueDateClick = onDueDateClick,
                    onRemoveDueDate = onRemoveDueDate
                )

            } ?: AddDueDate(onDueDateClick = onDueDateClick)

        }


    }
}

@Composable
fun DueDate(
    modifier: Modifier = Modifier,
    dueDate: String,
    onDueDateClick: () -> Unit,
    onRemoveDueDate: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Icon(
            imageVector = Icons.Outlined.Event,
            contentDescription = null
        )

        TextButton(
            onClick = onDueDateClick,
        ) {
            Text(text = dueDate, style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(Modifier.weight(1f))

        IconButton(
            onClick = onRemoveDueDate
        ) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = stringResource(R.string.card_button_remove_due_date)
            )
        }
    }
}

@Composable
private fun AddDueDate(modifier: Modifier = Modifier, onDueDateClick: () -> Unit) {

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.Outlined.Event,
            contentDescription = null
        )

        Spacer(Modifier.width(8.dp))
        Text(text = stringResource(R.string.card_text_add_due_date))

        Spacer(Modifier.weight(1f))
        IconButton(
            onClick = onDueDateClick
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.card_button_add_due_date)
            )
        }

    }
}