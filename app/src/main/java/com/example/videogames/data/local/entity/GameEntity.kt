package com.example.videogames.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val rating: Double,
    val genres: String,
    val releaseDate: String,
    val description: String,
    val page: Int,
    val genreFilter: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

