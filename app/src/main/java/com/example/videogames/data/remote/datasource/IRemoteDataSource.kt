package com.example.videogames.data.remote.datasource

import com.example.videogames.data.remote.dto.GameDetailsDto
import com.example.videogames.data.remote.dto.GamesResponseDto
import com.example.videogames.data.remote.dto.GenresResponseDto

interface IRemoteDataSource {
    suspend fun getGames(
        genres: String,
        page: Int,
        pageSize: Int
    ): GamesResponseDto

    suspend fun getGameDetails(gameId: Int): GameDetailsDto

    suspend fun getGenres(): GenresResponseDto
}
