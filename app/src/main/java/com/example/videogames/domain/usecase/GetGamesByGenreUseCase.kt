package com.example.videogames.domain.usecase

import android.util.Log
import com.example.videogames.core.response.RepositoryResult
import com.example.videogames.core.response.Resource
import com.example.videogames.domain.model.Game
import com.example.videogames.domain.repository.GamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGamesByGenreUseCase @Inject constructor(
    private val repository: GamesRepository
) {
    operator fun invoke(genre: String, page: Int): Flow<Resource<List<Game>>> = flow {
        emit(Resource.Loading(isLoading = true))
        try {
            repository.getGames(genre, page).collect { result ->
                Log.d(this::class.simpleName,result.toString())

                when (result) {
                    is RepositoryResult.Success -> emit(Resource.Success(result.data))
                    is RepositoryResult.Error -> emit(Resource.Error(result.message))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        } finally {
            emit(Resource.Loading(isLoading = false))
        }

    }
}
