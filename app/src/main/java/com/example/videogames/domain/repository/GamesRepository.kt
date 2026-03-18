package com.example.videogames.domain.repository

import com.example.videogames.core.response.RepositoryResult
import com.example.videogames.domain.model.Game
import com.example.videogames.domain.model.GameDetails
import com.example.videogames.domain.model.Genre
import kotlinx.coroutines.flow.Flow

interface GamesRepository {

    fun getGames(genre: String, page: Int): Flow<RepositoryResult<List<Game>>>

    fun getGameDetails(gameId: Int): Flow<RepositoryResult<GameDetails>>

    fun getGenres(): Flow<RepositoryResult<List<Genre>>>
}

