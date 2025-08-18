package com.educost.kanone.presentation.screens.card

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CardUiState())
    val uiState = _uiState.asStateFlow()

    fun onIntent(intent: CardIntent) {

    }
}