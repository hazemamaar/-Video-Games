package com.example.videogames.di

import android.content.Context
import androidx.room.Room
import com.example.videogames.data.local.ILocalDataSource
import com.example.videogames.data.local.LocalDataSource
import com.example.videogames.data.local.VideoGamesDatabase
import com.example.videogames.data.local.dao.GameDao
import com.example.videogames.data.local.dao.GenreDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindLocalDataSource(
        localDataSource: LocalDataSource
    ): ILocalDataSource

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): VideoGamesDatabase {
            return Room.databaseBuilder(
                context,
                VideoGamesDatabase::class.java,
                "video_games_db"
            ).build()
        }

        @Provides
        @Singleton
        fun provideGameDao(database: VideoGamesDatabase): GameDao {
            return database.gameDao()
        }

        @Provides
        @Singleton
        fun provideGenreDao(database: VideoGamesDatabase): GenreDao {
            return database.genreDao()
        }
    }
}

