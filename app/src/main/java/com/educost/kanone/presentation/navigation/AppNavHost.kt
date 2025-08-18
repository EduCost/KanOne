package com.educost.kanone.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.educost.kanone.presentation.screens.board.BoardScreen
import com.educost.kanone.presentation.screens.card.CardScreen
import com.educost.kanone.presentation.screens.home.HomeScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination
    ) {

        composable<HomeDestination> {
            HomeScreen(
                onNavigateToBoard = { boardId ->
                    navController.navigate(BoardDestination(boardId))
                }
            )
        }

        composable<BoardDestination> { backStackEntry ->
            val boardId = backStackEntry.toRoute<BoardDestination>().boardId
            BoardScreen(
                boardId = boardId,
                onNavigateToCard = { cardId ->
                    navController.navigate(CardDestination(cardId))
                }
            )
        }

        composable<CardDestination> { backStackEntry ->
            val cardId = backStackEntry.toRoute<CardDestination>().cardId
            CardScreen(cardId = cardId)
        }

    }
}