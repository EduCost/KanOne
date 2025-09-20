package com.educost.kanone.presentation.navigation

import kotlinx.serialization.Serializable


// Parent Destinations
@Serializable
data object MainDestinations

@Serializable
data object SettingsDestinations


// Screens
@Serializable
data object HomeDestination

@Serializable
data class BoardDestination(val boardId: Long)

@Serializable
data class CardDestination(val cardId: Long)

@Serializable
data object SettingsRootDestination

@Serializable
data object SettingsThemeDestination

@Serializable
data object LogDestination

@Serializable
data class LogDetailDestination(val logEventJson: String)