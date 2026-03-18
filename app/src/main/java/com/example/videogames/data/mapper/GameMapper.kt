package com.example.videogames.data.mapper

import com.example.videogames.core.mapper.BaseMapper
import com.example.videogames.data.local.entity.GameEntity
import com.example.videogames.data.remote.dto.GameDetailsDto
import com.example.videogames.data.remote.dto.GameDto
import com.example.videogames.domain.model.Game
import com.example.videogames.domain.model.GameDetails

class GameDtoToGameEntityMapper(val page: Int, val genreFilter: String) :
    BaseMapper<GameDto, GameEntity>() {
    override fun from(from: GameDto): GameEntity {
        return GameEntity(
            id = from.id,
            name = from.name,
            imageUrl = from.backgroundImage.orEmpty(),
            rating = from.rating,
            genres = from.genres?.joinToString(",") { it.name }.orEmpty(),
            releaseDate = from.released.orEmpty(),
            description = from.descriptionRaw.orEmpty(),
            page = page,
            genreFilter = genreFilter
        )
    }

}

class GameEntityToGameMapper() : BaseMapper<GameEntity, Game>() {
    override fun from(from: GameEntity): Game {
        return Game(
            id = from.id,
            name = from.name,
            imageUrl = from.imageUrl,
            rating = from.rating,
            genres = if (from.genres.isBlank()) emptyList() else from.genres.split(",")
                .map { it.trim() }
        )
    }

}


class GameEntityToGameDetailsMapper : BaseMapper<GameEntity, GameDetails>() {
    override fun from(from: GameEntity): GameDetails {
        return GameDetails(
            id = from.id,
            name = from.name,
            imageUrl = from.imageUrl,
            rating = from.rating,
            releaseDate = from.releaseDate,
            description = from.description,
            genres = if (from.genres.isBlank()) emptyList() else from.genres.split(",")
                .map { it.trim() },
            publishers = emptyList(),
            platforms = emptyList()
        )
    }

}

class GameDetailsDtoToGameEntityMapper() : BaseMapper<GameDetailsDto, GameEntity>() {
    override fun from(from: GameDetailsDto): GameEntity {
        return GameEntity(
            id = from.id,
            name = from.name,
            imageUrl = from.backgroundImage.orEmpty(),
            rating = from.rating,
            genres = from.genres?.joinToString(",") { it.name }.orEmpty(),
            releaseDate = from.released.orEmpty(),
            description = from.descriptionRaw.orEmpty(),
            page = 0,
            genreFilter = from.genres?.firstOrNull()?.slug.orEmpty()
        )
    }

}

class GameDetailsDtoToGameDetailsMapper() : BaseMapper<GameDetailsDto, GameDetails>() {
    override fun from(from: GameDetailsDto): GameDetails {
        return GameDetails(
            id = from.id,
            name = from.name,
            imageUrl = from.backgroundImage.orEmpty(),
            rating = from.rating,
            releaseDate = from.released.orEmpty(),
            description = from.descriptionRaw.orEmpty(),
            genres = from.genres?.map { it.name }.orEmpty(),
            publishers = from.publishers?.map { it.name }.orEmpty(),
            platforms = from.platforms?.map { it.platform.name }.orEmpty()
        )
    }

}


