package com.example.videogames.data.local

import com.example.videogames.data.local.entity.GameEntity
import com.example.videogames.data.local.entity.GenreEntity
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    fun getAllGenres(): Flow<List<GenreEntity>>
    suspend fun insertGenres(genres: List<GenreEntity>)
    suspend fun getGenresCount(): Int
    
    fun getGamesByGenre(genre: String): Flow<List<GameEntity>>
    fun getGameById(gameId: Int): Flow<GameEntity?>
    suspend fun insertGames(games: List<GameEntity>)
    suspend fun insertGame(game: GameEntity)
    suspend fun deleteGamesByGenre(genre: String)
    suspend fun getGamesCount(genre: String): Int
}
