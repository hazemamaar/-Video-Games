package com.example.videogames.ui.screens.gameslist

import com.example.videogames.domain.model.Game
import com.example.videogames.domain.model.Genre
import com.example.videogames.core.viewmodel.State

sealed interface GamesListState : State {
    data object Idle : GamesListState
    
    data class Loaded(
        val games: List<Game> = emptyList(),
        val filteredGames: List<Game> = emptyList(),
        val genres: List<Genre> = emptyList(),
        val selectedGenre: Genre? = null,
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val isPaginationLoading: Boolean = false,
        val error: String? = null,
        val isEmptyState: Boolean = false,
        val currentPage: Int = 1,
        val hasMorePages: Boolean = true
    ) : GamesListState
}