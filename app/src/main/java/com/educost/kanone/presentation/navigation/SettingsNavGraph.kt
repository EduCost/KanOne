package com.educost.kanone.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.educost.kanone.presentation.screens.settings.root.SettingsScreen
import com.educost.kanone.presentation.screens.settings.theme.SettingsThemeScreen

fun NavGraphBuilder.settingsNavGraph(navController: NavHostController) {
    navigation<SettingsDestinations>(startDestination = SettingsRootDestination) {

        composable<SettingsRootDestination> {
            SettingsScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToSettingsTheme = {
                    navController.navigate(SettingsThemeDestination)
                }
            )
        }

        composable<SettingsThemeDestination> {
            SettingsThemeScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

    }
}