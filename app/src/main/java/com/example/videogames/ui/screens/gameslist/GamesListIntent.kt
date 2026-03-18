package com.example.videogames.ui.screens.gameslist

import com.example.videogames.core.viewmodel.Intent
import com.example.videogames.domain.model.Genre

sealed class GamesListIntent : Intent {
    data object LoadGames : GamesListIntent()
    data object LoadNextPage : GamesListIntent()
    data class SearchQuery(val query: String) : GamesListIntent()
    data class SelectGenre(val genre: Genre) : GamesListIntent()
    data object Retry : GamesListIntent()
}

