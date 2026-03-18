package com.example.videogames.di

import com.example.videogames.core.network.INetworkProvider
import com.example.videogames.core.network.NetworkProvider
import com.example.videogames.data.remote.RawgApiService
import com.example.videogames.data.remote.RemoteInterceptor
import com.example.videogames.data.remote.datasource.IRemoteDataSource
import com.example.videogames.data.remote.datasource.RemoteDataSource
import com.example.videogames.utils.Constants
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindNetworkProvider(
        retrofitNetworkProvider: NetworkProvider
    ): INetworkProvider

    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(
        remoteDataSource: RemoteDataSource
    ): IRemoteDataSource

    companion object {
        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(RemoteInterceptor())
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        @Provides
        @Singleton
        fun provideRawgApiService(retrofit: Retrofit): RawgApiService {
            return retrofit.create(RawgApiService::class.java)
        }

    }
}

