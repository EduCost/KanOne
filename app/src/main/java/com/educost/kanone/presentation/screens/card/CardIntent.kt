package com.educost.kanone.presentation.screens.card

interface CardIntent {

    data class ObserveCard(val cardId: Long) : CardIntent
}