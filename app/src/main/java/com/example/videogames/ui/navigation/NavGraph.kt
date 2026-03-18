package com.example.videogames.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.videogames.ui.screens.gamedetails.GameDetailsScreen
import com.example.videogames.ui.screens.gameslist.GamesListScreen
import kotlinx.serialization.Serializable

sealed interface GamesRoutes {

    @Serializable
    data object GamesList : GamesRoutes

    @Serializable
    data class GameDetails(val gameId: Int) : GamesRoutes
}

@Composable
fun NavGraph(
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = GamesRoutes.GamesList
    ) {

        composable<GamesRoutes.GamesList> {
            GamesListScreen(
                onGameClick = { gameId ->
                    navController.navigate(
                        GamesRoutes.GameDetails(gameId)
                    )
                }
            )
        }

        composable<GamesRoutes.GameDetails> { backStackEntry ->

            val args = backStackEntry.toRoute<GamesRoutes.GameDetails>()

            GameDetailsScreen(
                gameId = args.gameId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

