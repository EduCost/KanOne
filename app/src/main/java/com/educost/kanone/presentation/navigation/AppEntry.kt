package com.educost.kanone.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.educost.kanone.presentation.theme.KanOneTheme

@Composable
fun AppEntry(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: AppEntryViewModel
) {

    val themeData by viewModel.themeData.collectAsStateWithLifecycle()

    themeData?.let { themeData ->
        KanOneTheme(
            themeData = themeData
        ) {
            NavHost(
                modifier = modifier,
                navController = navController,
                startDestination = MainDestinations
            ) {
                mainNavGraph(navController)
                settingsNavGraph(navController)
            }
        }
    }
}