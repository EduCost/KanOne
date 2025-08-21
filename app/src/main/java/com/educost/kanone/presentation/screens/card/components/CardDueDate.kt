package com.educost.kanone.presentation.screens.card.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.educost.kanone.R

@Composable
fun CardDueDate(modifier: Modifier = Modifier, dueDate: String?) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                1.dp
            )
        )
    ) {

        dueDate?.let { dueDate ->

            DueDate(dueDate = dueDate)

        } ?: AddDueDate()

    }
}

@Composable
private fun DueDate(modifier: Modifier = Modifier, dueDate: String) {
    Row(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Event,
            contentDescription = null
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.card_due_date) + ":\n$dueDate",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AddDueDate(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Text(text = "\n")

        TextButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Outlined.Event,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.card_add_due_date_button))
        }
    }
}