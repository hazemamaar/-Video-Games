package com.example.videogames.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.videogames.data.local.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM games WHERE genreFilter = :genre ORDER BY page ASC, name ASC")
    fun getGamesByGenre(genre: String): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :gameId")
    fun getGameById(gameId: Int): Flow<GameEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    @Query("DELETE FROM games WHERE genreFilter = :genre")
    suspend fun deleteGamesByGenre(genre: String)

    @Query("SELECT COUNT(*) FROM games WHERE genreFilter = :genre")
    suspend fun getGamesCount(genre: String): Int
}

