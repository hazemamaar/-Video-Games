package com.example.videogames.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.videogames.data.local.dao.GameDao
import com.example.videogames.data.local.dao.GenreDao
import com.example.videogames.data.local.entity.GameEntity
import com.example.videogames.data.local.entity.GenreEntity

@Database(
    entities = [GameEntity::class, GenreEntity::class],
    version = 1,
    exportSchema = false
)
abstract class VideoGamesDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun genreDao(): GenreDao
}

