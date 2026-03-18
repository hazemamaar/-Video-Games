package com.example.videogames.di

import com.example.videogames.core.error.ErrorHandler
import com.example.videogames.core.error.GeneralErrorHandlerImpl
import com.example.videogames.data.repository.GamesRepositoryImpl
import com.example.videogames.domain.repository.GamesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGamesRepository(
        gamesRepositoryImpl: GamesRepositoryImpl
    ): GamesRepository

    @Binds
    @Singleton
    abstract fun bindErrorHandler(
        generalErrorHandlerImpl: GeneralErrorHandlerImpl
    ): ErrorHandler
}

