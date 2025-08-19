package com.educost.kanone.presentation.screens.card

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.educost.kanone.presentation.theme.KanOneTheme

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

@PreviewLightDark
@Composable
private fun CardScreenPreview() {
    KanOneTheme {
        CardScreen(
            state = CardUiState(),
            onIntent = {}
        )
    }
}