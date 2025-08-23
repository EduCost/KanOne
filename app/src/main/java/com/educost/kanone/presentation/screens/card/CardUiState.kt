package com.educost.kanone.presentation.screens.card

import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.presentation.screens.card.utils.CardAppBarType

data class CardUiState(
    val card: CardItem? = null,
    val appBarType: CardAppBarType = CardAppBarType.DEFAULT,
    val newDescription: String? = null
)