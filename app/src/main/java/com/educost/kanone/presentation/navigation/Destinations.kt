package com.educost.kanone.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeDestination

@Serializable
data class BoardDestination(val boardId: Long)

@Serializable
data class CardDestination(val cardId: Long)