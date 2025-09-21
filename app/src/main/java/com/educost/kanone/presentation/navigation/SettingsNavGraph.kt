package com.educost.kanone.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.educost.kanone.presentation.screens.logs.logdetail.LogDetailScreen
import com.educost.kanone.presentation.screens.logs.loglist.LogScreen
import com.educost.kanone.presentation.screens.settings.about.AboutScreen
import com.educost.kanone.presentation.screens.settings.about.CreditsScreen
import com.educost.kanone.presentation.screens.settings.root.SettingsScreen
import com.educost.kanone.presentation.screens.settings.theme.SettingsThemeScreen

fun NavGraphBuilder.settingsNavGraph(navController: NavHostController) {
    navigation<SettingsDestinations>(startDestination = SettingsRootDestination) {

        composable<SettingsRootDestination> {
            SettingsScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToSettingsTheme = {
                    navController.navigate(SettingsThemeDestination)
                },
                onNavigateToAbout = {
                    navController.navigate(AboutDestination)
                },
                onNavigateToLog = {
                    navController.navigate(LogDestination)
                }
            )
        }

        composable<SettingsThemeDestination> {
            SettingsThemeScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<LogDestination> {
            LogScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToLogDetail = { logEvent ->
                    navController.navigate(LogDetailDestination(logEvent))
                }
            )
        }

        composable<LogDetailDestination> { backStackEntry ->
            val logEventJson = backStackEntry.toRoute<LogDetailDestination>().logEventJson
            LogDetailScreen(
                logEventJson = logEventJson,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<AboutDestination> {
            AboutScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToCredits = {
                    navController.navigate(CreditsDestination)
                }
            )
        }

        composable<CreditsDestination> {
            CreditsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

    }
}