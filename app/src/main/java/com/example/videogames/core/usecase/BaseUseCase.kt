package com.example.videogames.core.usecase

import com.example.videogames.core.response.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class BaseUseCase<Input, OutPut> {

    operator fun invoke(params: Input): Flow<Resource<OutPut?>> = flow {
        emit(Resource.Loading(true))
        try {
            emit(Resource.Success(execute(params)))
        } catch (t: Throwable) {
            emit(Resource.Error(t.message!!))
        }
        emit(Resource.Loading(false))
    }

    protected abstract suspend fun execute(params: Input): OutPut?
}