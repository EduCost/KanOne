package com.educost.kanone.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.educost.kanone.presentation.screens.board.BoardScreen
import com.educost.kanone.presentation.screens.card.CardScreen
import com.educost.kanone.presentation.screens.home.HomeScreen
import com.educost.kanone.presentation.screens.markdown.MarkdownScreen

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    navigation<MainDestinations>(startDestination = HomeDestination) {

        composable<HomeDestination> {
            HomeScreen(
                onNavigateToBoard = { boardId ->
                    navController.navigate(BoardDestination(boardId))
                },
                onNavigateToSettings = {
                    navController.navigate(SettingsRootDestination)
                }
            )
        }

        composable<BoardDestination> { backStackEntry ->
            val boardId = backStackEntry.toRoute<BoardDestination>().boardId
            BoardScreen(
                boardId = boardId,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToCard = { cardId ->
                    navController.navigate(CardDestination(cardId))
                },
                onNavigateToSettings = {
                    navController.navigate(SettingsRootDestination)
                }
            )
        }

        composable<CardDestination> { backStackEntry ->
            val cardId = backStackEntry.toRoute<CardDestination>().cardId
            CardScreen(
                cardId = cardId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToMarkdown = { navController.navigate(MarkdownDestination(cardId)) }
            )
        }

        composable<MarkdownDestination> {
            val cardId = it.toRoute<MarkdownDestination>().cardId
            MarkdownScreen(
                cardId = cardId
            )
        }

    }

}