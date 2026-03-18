package com.example.videogames.data.repository

import com.example.videogames.core.error.ErrorHandler
import com.example.videogames.core.response.RepositoryResult
import com.example.videogames.core.VideoGameException
import com.example.videogames.data.local.ILocalDataSource
import com.example.videogames.data.local.entity.GameEntity
import com.example.videogames.data.local.entity.GenreEntity
import com.example.videogames.data.mapper.GameDetailsDtoToGameDetailsMapper
import com.example.videogames.data.mapper.GameDetailsDtoToGameEntityMapper
import com.example.videogames.data.mapper.GameDtoToGameEntityMapper
import com.example.videogames.data.mapper.GameEntityToGameDetailsMapper
import com.example.videogames.data.mapper.GameEntityToGameMapper
import com.example.videogames.data.mapper.GenreDtoToGenreEntityMapper
import com.example.videogames.data.mapper.GenreEntityToGenreMapper
import com.example.videogames.data.remote.dto.GameDetailsDto
import com.example.videogames.data.remote.dto.GameDto
import com.example.videogames.data.remote.dto.GamesResponseDto
import com.example.videogames.data.remote.dto.GenreDto
import com.example.videogames.data.remote.dto.GenresResponseDto
import com.example.videogames.data.remote.datasource.IRemoteDataSource
import com.example.videogames.domain.model.Game
import com.example.videogames.domain.model.GameDetails
import com.example.videogames.domain.model.Genre
import com.example.videogames.domain.repository.GamesRepository
import com.example.videogames.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GamesRepositoryImpl @Inject constructor(
    private val remoteDataSource: IRemoteDataSource,
    private val localDataSource: ILocalDataSource,
    private val errorHandler: ErrorHandler
) : GamesRepository {

    private val gameEntityToGameMapper = GameEntityToGameMapper()
    private val gameEntityToGameDetailsMapper = GameEntityToGameDetailsMapper()
    private val gameDetailsDtoToGameDetailsMapper = GameDetailsDtoToGameDetailsMapper()
    private val gameDetailsDtoToGameEntityMapper = GameDetailsDtoToGameEntityMapper()
    private val genreEntityToGenreMapper = GenreEntityToGenreMapper()
    private val genreDtoToGenreEntityMapper = GenreDtoToGenreEntityMapper()

    override fun getGames(genre: String, page: Int): Flow<RepositoryResult<List<Game>>> = flow {
        val localGames = getLocalGamesSafely(genre)

        if (!localGames.isNullOrEmpty()) {
            val validatedLocalGames = validateLocalGames(localGames)
            if (validatedLocalGames.isNotEmpty()) {
                emit(RepositoryResult.Success(validatedLocalGames.map { gameEntityToGameMapper.from(it) }))
            }
        }

        try {
            val remoteResponse = remoteDataSource.getGames(
                genres = genre,
                page = page,
                pageSize = Constants.PAGE_SIZE
            )

            validateRemoteGamesResponse(remoteResponse)

            val gameEntities = remoteResponse.results.map { gameDto ->
                GameDtoToGameEntityMapper(page, genre).from(gameDto)
            }

            saveGamesLocally(gameEntities)

            val updatedLocalGames = getLocalGamesSafely(genre)
            if (updatedLocalGames != null && updatedLocalGames.isNotEmpty()) {
                val validatedGames = validateLocalGames(updatedLocalGames)
                emit(RepositoryResult.Success(validatedGames.map { gameEntityToGameMapper.from(it) }))
            } else {
                val validatedGames = remoteResponse.results.filter { validateGameDto(it) }
                emit(RepositoryResult.Success(validatedGames.map { gameEntityToGameMapper.from(GameDtoToGameEntityMapper(page, genre).from(it)) }))
            }
        } catch (e: Throwable) {
            if (localGames.isNullOrEmpty()) {
                val error = errorHandler.getError(e)
                emit(RepositoryResult.Error(error.message ?: "An error occurred"))
            }
        }
    }

    override fun getGameDetails(gameId: Int): Flow<RepositoryResult<GameDetails>> = flow {
        val localGame = getLocalGameSafely(gameId)

        val hasValidLocalDetails = localGame != null && localGame.description.isNotBlank()
        if (hasValidLocalDetails) {
            val validatedGame = validateLocalGame(localGame!!)
            if (validatedGame != null) {
                emit(RepositoryResult.Success(gameEntityToGameDetailsMapper.from(validatedGame)))
            }
        }

        try {
            val remoteResponse = remoteDataSource.getGameDetails(gameId)
            validateRemoteGameDetailsResponse(remoteResponse)

            val gameEntity = gameDetailsDtoToGameEntityMapper.from(remoteResponse)
            saveGameLocally(gameEntity)

            emit(RepositoryResult.Success(gameDetailsDtoToGameDetailsMapper.from(remoteResponse)))
        } catch (e: Throwable) {
            if (!hasValidLocalDetails) {
                val error = errorHandler.getError(e)
                emit(RepositoryResult.Error(error.message ?: "An error occurred"))
            }
        }
    }

    override fun getGenres(): Flow<RepositoryResult<List<Genre>>> = flow {
        val localGenres = getLocalGenresSafely()

        if (!localGenres.isNullOrEmpty()) {
            val validatedGenres = validateLocalGenres(localGenres)
            if (validatedGenres.isNotEmpty()) {
                emit(RepositoryResult.Success(validatedGenres.map { genreEntityToGenreMapper.from(it) }))
            }
        }

        try {
            val remoteResponse = remoteDataSource.getGenres()
            validateRemoteGenresResponse(remoteResponse)

            val genreEntities = remoteResponse.results.map { genreDto ->
                genreDtoToGenreEntityMapper.from(genreDto)
            }

            saveGenresLocally(genreEntities)

            val updatedLocalGenres = getLocalGenresSafely()
            if (updatedLocalGenres != null && updatedLocalGenres.isNotEmpty()) {
                val validatedGenres = validateLocalGenres(updatedLocalGenres)
                emit(RepositoryResult.Success(validatedGenres.map { genreEntityToGenreMapper.from(it) }))
            } else {
                val validatedGenres = remoteResponse.results.filter { validateGenreDto(it) }
                emit(RepositoryResult.Success(validatedGenres.map { genreEntityToGenreMapper.from(genreDtoToGenreEntityMapper.from(it)) }))
            }
        } catch (e: Throwable) {
            if (localGenres.isNullOrEmpty()) {
                val error = errorHandler.getError(e)
                emit(RepositoryResult.Error(error.message ?: "An error occurred"))
            }
        }
    }

    private suspend fun getLocalGamesSafely(genre: String): List<GameEntity>? {
        return try {
            localDataSource.getGamesByGenre(genre)
                .catch { e ->
                    null
                }
                .firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getLocalGameSafely(gameId: Int): GameEntity? {
        return try {
            localDataSource.getGameById(gameId)
                .catch { e -> null }
                .firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getLocalGenresSafely(): List<GenreEntity>? {
        return try {
            localDataSource.getAllGenres()
                .catch { e -> null }
                .firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun saveGamesLocally(games: List<GameEntity>) {
        try {
            localDataSource.insertGames(games)
        } catch (e: Exception) {
            throw errorHandler.getError(e)
        }
    }

    private suspend fun saveGameLocally(game: GameEntity) {
        try {
            localDataSource.insertGame(game)
        } catch (e: Exception) {
            throw errorHandler.getError(e)
        }
    }

    private suspend fun saveGenresLocally(genres: List<GenreEntity>) {
        try {
            localDataSource.insertGenres(genres)
        } catch (e: Exception) {
            throw errorHandler.getError(e)
        }
    }

    private fun validateRemoteGamesResponse(response: GamesResponseDto) {
        if (response.results.isEmpty() && response.count > 0) {
            throw VideoGameException.Client.ResponseValidation(
                message = "Response contains count but no results"
            )
        }
        response.results.forEach { gameDto ->
            if (!validateGameDto(gameDto)) {
                throw VideoGameException.Client.ResponseValidation(
                    message = "Invalid game data in response: game ID ${gameDto.id}"
                )
            }
        }
    }

    private fun validateRemoteGameDetailsResponse(response: GameDetailsDto) {
        if (!validateGameDetailsDto(response)) {
            throw VideoGameException.Client.ResponseValidation(
                message = "Invalid game details data in response: game ID ${response.id}"
            )
        }
    }

    private fun validateRemoteGenresResponse(response: GenresResponseDto) {
        if (response.results.isEmpty() && response.count > 0) {
            throw VideoGameException.Client.ResponseValidation(
                message = "Response contains count but no results"
            )
        }
        response.results.forEach { genreDto ->
            if (!validateGenreDto(genreDto)) {
                throw VideoGameException.Client.ResponseValidation(
                    message = "Invalid genre data in response: genre ID ${genreDto.id}"
                )
            }
        }
    }

    private fun validateGameDto(gameDto: GameDto): Boolean {
        return gameDto.id > 0 && gameDto.name.isNotBlank()
    }

    private fun validateGameDetailsDto(gameDetailsDto: GameDetailsDto): Boolean {
        return gameDetailsDto.id > 0 && gameDetailsDto.name.isNotBlank()
    }

    private fun validateGenreDto(genreDto: GenreDto): Boolean {
        return genreDto.id > 0 && genreDto.name.isNotBlank() && genreDto.slug.isNotBlank()
    }

    private fun validateLocalGames(games: List<GameEntity>): List<GameEntity> {
        return games.filter { validateLocalGame(it) != null }
    }

    private fun validateLocalGame(game: GameEntity): GameEntity? {
        return if (game.id > 0 && game.name.isNotBlank()) {
            game
        } else {
            null
        }
    }

    private fun validateLocalGenres(genres: List<GenreEntity>): List<GenreEntity> {
        return genres.filter { validateLocalGenre(it) != null }
    }

    private fun validateLocalGenre(genre: GenreEntity): GenreEntity? {
        return if (genre.id > 0 && genre.name.isNotBlank() && genre.slug.isNotBlank()) {
            genre
        } else {
            null
        }
    }
}
