package com.educost.kanone.presentation.screens.card

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CardScreen(
    modifier: Modifier = Modifier,
    cardViewModel: CardViewModel = hiltViewModel(),
    cardId: Long
) {
    val state by cardViewModel.uiState.collectAsStateWithLifecycle()

    CardScreen(
        modifier = modifier,
        state = state,
        onIntent = cardViewModel::onIntent
    )
}

@Composable
private fun CardScreen(
    modifier: Modifier = Modifier,
    state: CardUiState,
    onIntent: (CardIntent) -> Unit
) {

}