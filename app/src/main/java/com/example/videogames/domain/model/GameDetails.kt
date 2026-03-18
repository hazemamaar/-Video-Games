package com.example.videogames.domain.model

data class GameDetails(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val rating: Double,
    val releaseDate: String,
    val description: String,
    val genres: List<String>,
    val publishers: List<String>,
    val platforms: List<String>
)

