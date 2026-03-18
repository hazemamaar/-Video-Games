package com.example.videogames.ui.screens.gameslist.logic

import androidx.lifecycle.viewModelScope
import com.example.videogames.core.response.Resource
import com.example.videogames.core.viewmodel.BaseViewModel
import com.example.videogames.domain.model.Game
import com.example.videogames.domain.model.Genre
import com.example.videogames.domain.usecase.GetGamesByGenreUseCase
import com.example.videogames.domain.usecase.GetGenresUseCase
import com.example.videogames.ui.screens.gameslist.contract.GamesListIntent
import com.example.videogames.ui.screens.gameslist.contract.GamesListState
import com.example.videogames.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamesListViewModel @Inject constructor(
    private val getGamesByGenreUseCase: GetGamesByGenreUseCase,
    private val getGenresUseCase: GetGenresUseCase
) : BaseViewModel<GamesListState, GamesListIntent>(GamesListState.Idle) {

    init {
        super.sendIntent(GamesListIntent.LoadGames)
        this.loadGenres()
    }

    override fun handleIntent(intent: GamesListIntent) {
        when (intent) {
            is GamesListIntent.LoadGames -> this.loadGames(resetPage = true)
            is GamesListIntent.LoadNextPage -> this.loadNextPage()
            is GamesListIntent.SearchQuery -> this.search(intent.query)
            is GamesListIntent.SelectGenre -> this.selectGenre(intent.genre)
            is GamesListIntent.Retry -> {
                this.loadGenres()
                this.loadGames(resetPage = true)
            }
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            getGenresUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        updateState { currentState ->
                            when (currentState) {
                                is GamesListState.Idle -> {
                                    GamesListState.Loaded(genres = resource.data)
                                }
                                is GamesListState.Loaded -> {
                                    currentState.copy(genres = resource.data)
                                }
                            }
                        }

                        // Auto-select first genre if none selected
                        val currentState = viewState.value
                        if (currentState is GamesListState.Loaded) {
                            if (currentState.selectedGenre == null && resource.data.isNotEmpty()) {
                                val defaultGenre = resource.data.find {
                                    it.slug == Constants.DEFAULT_GENRE
                                } ?: resource.data.first()
                                selectGenre(defaultGenre)
                            }
                        }
                    }
                    is Resource.Error -> { /* genres error is non-fatal */ }
                    is Resource.Loading -> { /* ignore */ }
                }
            }
        }
    }

    private fun loadGames(resetPage: Boolean) {
        val currentState = viewState.value
        val genre = when (currentState) {
            is GamesListState.Idle -> Constants.DEFAULT_GENRE
            is GamesListState.Loaded -> currentState.selectedGenre?.slug ?: Constants.DEFAULT_GENRE
        }

        val page = when (currentState) {
            is GamesListState.Idle -> 1
            is GamesListState.Loaded -> if (resetPage) 1 else currentState.currentPage
        }

        if (resetPage) {
            updateState { state ->
                when (state) {
                    is GamesListState.Idle -> {
                        GamesListState.Loaded(
                            isLoading = true,
                            error = null,
                            currentPage = 1
                        )
                    }
                    is GamesListState.Loaded -> {
                        state.copy(isLoading = true, error = null, currentPage = 1)
                    }
                }
            }
        }

        viewModelScope.launch {
            getGamesByGenreUseCase(genre, page).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        if (resetPage) {
                            updateState { state ->
                                when (state) {
                                    is GamesListState.Idle -> {
                                        GamesListState.Loaded(
                                            isLoading = resource.isLoading
                                        )
                                    }
                                    is GamesListState.Loaded -> {
                                        state.copy(isLoading = resource.isLoading)
                                    }
                                }
                            }
                        }
                    }
                    is Resource.Success -> {
                        val games = resource.data
                        updateState { state ->
                                when (state) {
                                    is GamesListState.Idle -> {
                                        GamesListState.Loaded(
                                            games = games,
                                            filteredGames = games,
                                            isLoading = false,
                                            error = null,
                                            isEmptyState = games.isEmpty(),
                                            hasMorePages = games.size >= Constants.PAGE_SIZE
                                        )
                                    }
                                    is GamesListState.Loaded -> {
                                    val allGames = if (resetPage) {
                                        games
                                    } else {
                                        val existingIds = state.games.map { it.id }.toSet()
                                        state.games + games.filter { it.id !in existingIds }
                                    }
                                    val filtered = filterGames(allGames, state.searchQuery)
                                    state.copy(
                                        games = allGames,
                                        filteredGames = filtered,
                                        isLoading = false,
                                        isPaginationLoading = false,
                                        error = null,
                                        isEmptyState = filtered.isEmpty(),
                                        hasMorePages = games.size >= Constants.PAGE_SIZE
                                    )
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        updateState { state ->
                                when (state) {
                                    is GamesListState.Idle -> {
                                        GamesListState.Loaded(
                                            error = resource.message,
                                            isEmptyState = true
                                        )
                                    }
                                    is GamesListState.Loaded -> {
                                    state.copy(
                                        error = resource.message,
                                        isLoading = false,
                                        isPaginationLoading = false,
                                        isEmptyState = state.games.isEmpty()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadNextPage() {
        val currentState = viewState.value
        if (currentState is GamesListState.Loaded) {
            if (currentState.isPaginationLoading || !currentState.hasMorePages) return

            updateState { state ->
                when (state) {
                    is GamesListState.Idle -> state
                    is GamesListState.Loaded -> {
                        state.copy(
                            isPaginationLoading = true,
                            currentPage = state.currentPage + 1
                        )
                    }
                }
            }
            loadGames(resetPage = false)
        }
    }

    private fun search(query: String) {
        updateState { state ->
            when (state) {
                is GamesListState.Idle -> state
                is GamesListState.Loaded -> {
                    val filtered = filterGames(state.games, query)
                    state.copy(
                        searchQuery = query,
                        filteredGames = filtered,
                        isEmptyState = filtered.isEmpty() && state.games.isNotEmpty()
                    )
                }
            }
        }
    }

    private fun selectGenre(genre: Genre) {
        updateState { state ->
            when (state) {
                is GamesListState.Idle -> {
                    GamesListState.Loaded(
                        selectedGenre = genre,
                        games = emptyList(),
                        filteredGames = emptyList(),
                        searchQuery = ""
                    )
                }
                is GamesListState.Loaded -> {
                    state.copy(
                        selectedGenre = genre,
                        games = emptyList(),
                        filteredGames = emptyList(),
                        searchQuery = ""
                    )
                }
            }
        }
        loadGames(resetPage = true)
    }

    private fun filterGames(games: List<Game>, query: String): List<Game> {
        if (query.isBlank()) return games
        return games.filter { game ->
            game.name.contains(query, ignoreCase = true) ||
                game.genres.any { it.contains(query, ignoreCase = true) }
        }
    }

    private fun updateState(update: (GamesListState) -> GamesListState) {
        super.setState(update(viewState.value))
    }
}