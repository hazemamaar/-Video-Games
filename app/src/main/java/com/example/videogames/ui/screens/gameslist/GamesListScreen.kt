package com.example.videogames.ui.screens.gameslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.videogames.ui.components.EmptyState
import com.example.videogames.ui.components.ErrorState
import com.example.videogames.ui.components.FullScreenLoading
import com.example.videogames.ui.components.GameCard
import com.example.videogames.ui.components.GenreChips
import com.example.videogames.ui.components.PaginationLoading
import com.example.videogames.ui.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesListScreen(
    onGameClick: (Int) -> Unit,
    viewModel: GamesListViewModel = hiltViewModel()
) {
    val state by viewModel.viewState.collectAsState()
    val listState = rememberLazyListState()
    // Pagination trigger
    val shouldLoadMore by remember {
        derivedStateOf {
            when (val gamesList = state) {
                is GamesListState.Idle -> false
                is GamesListState.Loaded -> {

                    val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                    val totalItems = listState.layoutInfo.totalItemsCount
                    lastVisibleItem >= totalItems - 3 && !gamesList.isPaginationLoading && gamesList.hasMorePages
                }
            }
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && state is GamesListState.Loaded) {
            val loadedState = state as GamesListState.Loaded
            if (loadedState.filteredGames.isNotEmpty()) {
                viewModel.sendIntent(GamesListIntent.LoadNextPage)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Video Games",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val gamesList = state) {
                is GamesListState.Idle -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        FullScreenLoading()
                    }
                }
                is GamesListState.Loaded -> {
                    SearchBar(
                        query = gamesList.searchQuery,
                        onQueryChange = { viewModel.sendIntent(GamesListIntent.SearchQuery(it)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (gamesList.genres.isNotEmpty()) {
                        GenreChips(
                            genres = gamesList.genres,
                            selectedGenre = gamesList.selectedGenre,
                            onGenreSelected = { viewModel.sendIntent(GamesListIntent.SelectGenre(it)) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        when {
                            gamesList.isLoading && gamesList.games.isEmpty() -> {
                                FullScreenLoading()
                            }
                            gamesList.error != null && gamesList.games.isEmpty() -> {
                                ErrorState(
                                    message = gamesList.error,
                                    onRetry = { viewModel.sendIntent(GamesListIntent.Retry) }
                                )
                            }
                            gamesList.isEmptyState -> {
                                EmptyState(
                                    message = if (gamesList.searchQuery.isNotBlank()) {
                                        "No games match \"${gamesList.searchQuery}\""
                                    } else {
                                        "No games available for this genre"
                                    }
                                )
                            }
                            else -> {
                                LazyColumn(
                                    state = listState,
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(
                                        items = gamesList.filteredGames,
                                        key = { it.id }
                                    ) { game ->
                                        GameCard(
                                            game = game,
                                            onClick = { onGameClick(game.id) }
                                        )
                                    }

                                    if (gamesList.isPaginationLoading) {
                                        item {
                                            PaginationLoading()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

