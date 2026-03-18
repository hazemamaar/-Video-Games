package com.example.videogames.domain.usecase

import com.example.videogames.core.response.RepositoryResult
import com.example.videogames.core.response.Resource
import com.example.videogames.domain.model.Genre
import com.example.videogames.domain.repository.GamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val repository: GamesRepository
) {
    operator fun invoke(): Flow<Resource<List<Genre>>> = flow {
        emit(Resource.Loading(isLoading = true))
        try {
            repository.getGenres().collect { result ->
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
