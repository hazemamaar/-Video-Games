package com.example.videogames.data.local

import com.example.videogames.data.local.dao.GameDao
import com.example.videogames.data.local.dao.GenreDao
import com.example.videogames.data.local.entity.GameEntity
import com.example.videogames.data.local.entity.GenreEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val gameDao: GameDao,
    private val genreDao: GenreDao
) : ILocalDataSource {

    override fun getAllGenres(): Flow<List<GenreEntity>> {
        return genreDao.getAllGenres()
    }

    override suspend fun insertGenres(genres: List<GenreEntity>) {
        genreDao.insertGenres(genres)
    }

    override suspend fun getGenresCount(): Int {
        return genreDao.getGenresCount()
    }

    override fun getGamesByGenre(genre: String): Flow<List<GameEntity>> {
        return gameDao.getGamesByGenre(genre)
    }

    override fun getGameById(gameId: Int): Flow<GameEntity?> {
        return gameDao.getGameById(gameId)
    }

    override suspend fun insertGames(games: List<GameEntity>) {
        gameDao.insertGames(games)
    }

    override suspend fun insertGame(game: GameEntity) {
        gameDao.insertGame(game)
    }

    override suspend fun deleteGamesByGenre(genre: String) {
        gameDao.deleteGamesByGenre(genre)
    }

    override suspend fun getGamesCount(genre: String): Int {
        return gameDao.getGamesCount(genre)
    }
}