package com.educost.kanone.presentation.screens.card.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.educost.kanone.R
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.presentation.screens.card.CardIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardAppBar(modifier: Modifier = Modifier, card: CardItem, onIntent: (CardIntent) -> Unit) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = card.title)
        },
        navigationIcon = {
            IconButton(
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.navigate_back_button_content_description)
                )
            }
        },
        actions = {
            IconButton(
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.icon_more_vert_content_description)
                )
            }
        }
    )
}