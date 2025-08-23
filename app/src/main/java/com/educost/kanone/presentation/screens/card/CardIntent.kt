package com.educost.kanone.presentation.screens.card

sealed interface CardIntent {

    data class ObserveCard(val cardId: Long) : CardIntent

    // Description
    data object StartEditingDescription : CardIntent
    data class OnDescriptionChanged(val description: String) : CardIntent
    data object SaveDescription : CardIntent
    data object CancelEditingDescription : CardIntent
}