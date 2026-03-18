package com.example.videogames.data.remote

import com.example.videogames.data.remote.dto.GameDetailsDto
import com.example.videogames.data.remote.dto.GamesResponseDto
import com.example.videogames.data.remote.dto.GenresResponseDto
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface RawgApiService {

//    @GET("games")
//    suspend fun getGames(
//        @Query("genres") genres: String,
//        @Query("page") page: Int,
//        @Query("page_size") pageSize: Int
//    ): GamesResponseDto

    @GET("{pathUrl}")
    @JvmSuppressWildcards
    suspend fun get(
        @Path(value = "pathUrl", encoded = true) pathUrl: String,
        @HeaderMap headerMap: Map<String, Any>, @QueryMap queryParams: Map<String, Any>
    ): ResponseBody

//    @GET("games/{id}")
//    suspend fun getGameDetails(
//        @Path("id") gameId: Int,
//    ): GameDetailsDto
//
//    @GET("genres")
//    suspend fun getGenres(): GenresResponseDto
}

