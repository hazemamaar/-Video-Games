package com.example.videogames.data.remote.datasource

import com.example.videogames.core.network.INetworkProvider
import com.example.videogames.data.remote.dto.GameDetailsDto
import com.example.videogames.data.remote.dto.GamesResponseDto
import com.example.videogames.data.remote.dto.GenresResponseDto
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val provider: INetworkProvider
) : IRemoteDataSource {

    override suspend fun getGames(
        genres: String,
        page: Int,
        pageSize: Int
    ): GamesResponseDto {
        return provider.get(
            responseWrappedModel = GamesResponseDto::class.java,
            pathUrl = "games",
            queryParams = mapOf(
                "genres" to genres,
                "page" to page,
                "page_size" to pageSize
            )
        )
    }

    override suspend fun getGameDetails(gameId: Int): GameDetailsDto {
        return provider.get(
            responseWrappedModel = GameDetailsDto::class.java,
            pathUrl = "games/$gameId"
        )
    }

    override suspend fun getGenres(): GenresResponseDto {
        return provider.get(
            responseWrappedModel = GenresResponseDto::class.java,
            pathUrl = "genres"
        )
    }
}